package cn.edu.sicnu.cs.why.ex6

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.why.ex6.data.GameStore
import cn.edu.sicnu.cs.why.ex6.game.CardMatchingGame
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 两个翻牌游戏 Fragment 的公共实现。
 *
 * 子类只需要给出牌数、列数、标题和规则；两张牌 / 三张牌的区别
 * 由 [cardsPerMatch] 控制。整个游戏对象在每次操作后都会序列化到
 * 对应文件，退出应用再打开时由 [GameStore] 恢复。
 */
abstract class GameFragment : Fragment() {

    protected abstract val cardsPerMatch: Int
    protected abstract val cardCountRes: Int
    protected abstract val columnCountRes: Int
    protected abstract val titleRes: Int
    protected abstract val ruleRes: Int

    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter
    private lateinit var rvCards: RecyclerView
    private lateinit var tvScore: TextView
    private lateinit var tvBest: TextView
    private lateinit var tvHistory: TextView

    private val timeFormat = SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        val cardCount = resources.getInteger(cardCountRes)
        val columns = resources.getInteger(columnCountRes)

        val savedGame = GameStore.load(context, cardsPerMatch)
        game = if (savedGame != null && savedGame.cardCount == cardCount) {
            savedGame
        } else {
            CardMatchingGame(cardCount, cardsPerMatch)
        }

        view.findViewById<TextView>(R.id.tvGameTitle).setText(titleRes)
        view.findViewById<TextView>(R.id.tvRule).setText(ruleRes)

        tvScore = view.findViewById(R.id.tvScore)
        tvBest = view.findViewById(R.id.tvBest)
        tvHistory = view.findViewById(R.id.tvHistory)
        rvCards = view.findViewById(R.id.rvCards)

        rvCards.layoutManager = GridLayoutManager(context, columns)
        adapter = CardAdapter(game) { index -> onCardClicked(index) }
        rvCards.adapter = adapter

        // RecyclerView 的高度要等整页布局完成后才是最终值，监听尺寸变化，
        // 每次变化都重新计算牌的大小，保证所有牌都能完整显示。
        rvCards.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val size = calculateCardSize(rvCards, columns, cardCount)
            rvCards.post { adapter.setCardSize(size) }
        }

        view.findViewById<View>(R.id.btnReset).setOnClickListener { startNewGame() }

        updateUi()
    }

    override fun onStop() {
        super.onStop()
        saveGame()
    }

    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)
        adapter.notifyDataSetChanged()
        updateUi()
        // 每次操作后立即保存，避免进程被系统回收时丢失进度。
        saveGame()
    }

    private fun startNewGame() {
        game.reset()
        adapter.notifyDataSetChanged()
        updateUi()
        saveGame()
    }

    private fun updateUi() {
        tvScore.text = getString(R.string.score_format, game.score)
        tvBest.text = getString(R.string.best_score_format, game.bestScore)
        tvHistory.text = buildHistoryText()
    }

    private fun buildHistoryText(): String {
        val header = getString(R.string.history_title)
        if (game.history.isEmpty()) {
            return header + "\n" + getString(R.string.history_empty)
        }

        val recent = game.history.takeLast(3).joinToString("\n") { record ->
            val time = timeFormat.format(Date(record.time))
            val result = getString(
                if (record.matched) R.string.history_match else R.string.history_miss,
                record.cards,
                record.totalScore
            )
            "$time  $result"
        }
        return header + "\n" + recent
    }

    private fun saveGame() {
        val context = context ?: return
        GameStore.save(context, game)
    }

    /** 计算每个格子中能放下的最大正方形，横竖屏都能保持整齐。 */
    private fun calculateCardSize(
        recycler: RecyclerView,
        columns: Int,
        cardCount: Int
    ): Int {
        if (columns <= 0 || cardCount <= 0) return 0
        val rows = (cardCount + columns - 1) / columns
        val spacing = dp(6)

        val availableWidth =
            recycler.width - recycler.paddingLeft - recycler.paddingRight
        val availableHeight =
            recycler.height - recycler.paddingTop - recycler.paddingBottom

        val cellWidth = availableWidth / columns
        val cellHeight = availableHeight / rows
        return maxOf(minOf(cellWidth, cellHeight) - spacing, dp(40))
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
