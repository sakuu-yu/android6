package cn.edu.sicnu.cs.why.ex6.game

import java.io.Serializable

/**
 * 一条翻牌历史记录。
 *
 * 记录会随 [CardMatchingGame] 一起序列化到文件中，应用重新打开后，
 * 界面上仍然可以看到之前的配对记录。
 */
data class GameRecord(
    val time: Long,
    val matched: Boolean,
    val cards: String,
    val delta: Int,
    val totalScore: Int
) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
