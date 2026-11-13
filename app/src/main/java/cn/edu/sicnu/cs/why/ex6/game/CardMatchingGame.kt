package cn.edu.sicnu.cs.why.ex6.game

import java.io.Serializable

/**
 * 翻牌配对游戏的模型。
 *
 * 同一个类同时支持两张牌和三张牌两种玩法：构造时传入
 * [cardsPerMatch]（2 或 3）即可。整个对象实现 [Serializable]，
 * 保存了牌面、选中/配对状态、得分、最高分和最近的历史记录，
 * 因此退出应用后可以通过对象序列化完整恢复。
 */
class CardMatchingGame(
    private val initialCardCount: Int,
    val cardsPerMatch: Int = Card.CARDS_PER_MATCH
) : Serializable {

    var score = 0
        private set

    /** 历史最高分；重置游戏后仍然保留。 */
    var bestScore = 0
        private set

    private val cards = mutableListOf<Card>()

    /** 最近的配对记录，最新的记录在最后。 */
    val history = mutableListOf<GameRecord>()

    init {
        reset()
    }

    /** 重新发牌并清零本局得分，历史记录也一并清空。 */
    fun reset() {
        score = 0
        cards.clear()
        history.clear()

        val deck = Deck()
        repeat(initialCardCount) {
            deck.drawRandomCard()?.let(cards::add)
        }
    }

    fun cardAtIndex(index: Int): Card = cards[index]

    val cardCount: Int
        get() = cards.size

    /**
     * 翻开指定位置的牌。
     *
     * - 已经配对的牌不能再翻；
     * - 再次点击已翻开的牌会把它盖回去，且不扣分；
     * - 当翻开的牌达到 [cardsPerMatch] 张时判断是否配对。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) return

        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val chosenCards = cards.filter { it.isChosen && !it.isMatched }
        var delta = -COST_TO_CHOOSE
        var matched = false
        var faces = ""

        if (chosenCards.size >= cardsPerMatch - 1) {
            val otherCards = chosenCards
                .take(cardsPerMatch - 1)
                .toTypedArray()
            val matchScore = card.match(otherCards, cardsPerMatch)
            faces = (listOf(card) + otherCards)
                .joinToString(" ") { it.toString() }

            if (matchScore > 0) {
                delta += matchScore * MATCH_BONUS
                card.isMatched = true
                otherCards.forEach { it.isMatched = true }
                matched = true
            } else {
                delta -= MISMATCH_PENALTY
                otherCards.forEach { it.isChosen = false }
            }
        }

        score += delta
        if (score > bestScore) bestScore = score

        if (faces.isNotEmpty()) {
            history.add(
                GameRecord(
                    time = System.currentTimeMillis(),
                    matched = matched,
                    cards = faces,
                    delta = delta,
                    totalScore = score
                )
            )
            while (history.size > MAX_HISTORY) {
                history.removeAt(0)
            }
        }

        card.isChosen = true
    }

    companion object {
        private const val serialVersionUID = 1L

        const val MISMATCH_PENALTY = 2
        const val MATCH_BONUS = 4
        const val COST_TO_CHOOSE = 1
        const val MAX_HISTORY = 20
    }
}
