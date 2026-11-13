package cn.edu.sicnu.cs.why.ex6.game

import java.io.Serializable

/**
 * 一张扑克牌。
 *
 * 实现 [Serializable] 后，整局游戏（包括每张牌是否被选中、是否已配对）
 * 可以直接写入文件，退出应用后再读回来。
 */
class Card(
    val suit: String,
    val rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L

        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")

        /** 默认情况下连续翻开三张牌进行配对。 */
        const val CARDS_PER_MATCH = 3

        /** 点数全部相同时的得分。 */
        const val RANK_MATCH_SCORE = 8

        /** 花色全部相同时的得分。 */
        const val SUIT_MATCH_SCORE = 4
    }

    override fun toString(): String = suit + rank

    /** 红桃和方块显示为红色。 */
    val isRed: Boolean
        get() = suit == "♥" || suit == "♦"

    /**
     * 判断当前牌与 [otherCards] 能否组成一次配对。
     *
     * [cardsPerMatch] 为 2 时是两张牌的游戏，为 3 时是三张牌的游戏。
     * 所有牌点数相同返回高分，所有牌花色相同返回低分，否则返回 0。
     */
    fun match(otherCards: Array<Card>, cardsPerMatch: Int = CARDS_PER_MATCH): Int {
        if (cardsPerMatch < 2 || otherCards.size != cardsPerMatch - 1) return 0

        val allCards = arrayOf(this) + otherCards
        return when {
            allCards.all { it.rank == rank } -> RANK_MATCH_SCORE
            allCards.all { it.suit == suit } -> SUIT_MATCH_SCORE
            else -> 0
        }
    }
}
