package cn.edu.sicnu.cs.why.ex6.data

import android.content.Context
import cn.edu.sicnu.cs.why.ex6.game.CardMatchingGame

/** 两种翻牌游戏分别保存到不同的文件。 */
object GameStore {

    private const val FILE_TWO_CARDS = "game2.dat"
    private const val FILE_THREE_CARDS = "game3.dat"

    fun fileNameFor(cardsPerMatch: Int): String =
        if (cardsPerMatch == 2) FILE_TWO_CARDS else FILE_THREE_CARDS

    fun load(context: Context, cardsPerMatch: Int): CardMatchingGame? =
        SerializableStore.load(context, fileNameFor(cardsPerMatch))

    fun save(context: Context, game: CardMatchingGame) {
        SerializableStore.save(context, fileNameFor(game.cardsPerMatch), game)
    }

    /** 个人信息页展示最高分时使用。 */
    fun bestScore(context: Context, cardsPerMatch: Int): Int =
        load(context, cardsPerMatch)?.bestScore ?: 0
}
