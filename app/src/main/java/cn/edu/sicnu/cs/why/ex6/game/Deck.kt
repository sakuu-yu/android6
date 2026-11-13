package cn.edu.sicnu.cs.why.ex6.game

import java.util.Random

/** 一副 52 张的扑克牌，每次随机抽出一张。 */
class Deck {
    private val cards = mutableListOf<Card>()
    private val random = Random()

    init {
        for (suit in Card.validSuits) {
            for (rank in Card.rankStrings) {
                cards.add(Card(suit = suit, rank = rank))
            }
        }
    }

    fun drawRandomCard(): Card? {
        if (cards.isEmpty()) return null
        return cards.removeAt(random.nextInt(cards.size))
    }
}
