package cn.edu.sicnu.cs.why.ex6

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.why.ex6.game.Card
import cn.edu.sicnu.cs.why.ex6.game.CardMatchingGame

/**
 * RecyclerView 适配器（View 层）：只负责把 [CardMatchingGame] 中的牌
 * 显示出来，并把点击事件通过 [onCardClick] 交回给 Fragment（Controller）。
 */
class CardAdapter(
    private val game: CardMatchingGame,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var cardSize = 0

    fun setCardSize(size: Int) {
        if (size > 0 && size != cardSize) {
            cardSize = size
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(game.cardAtIndex(position), position, cardSize)
    }

    override fun getItemCount(): Int = game.cardCount

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cardInner: FrameLayout = itemView.findViewById(R.id.cardInner)
        private val cardFace: TextView = itemView.findViewById(R.id.tvCardFace)
        private val cardBack: View = itemView.findViewById(R.id.vCardBack)
        private var boundPosition = RecyclerView.NO_POSITION

        init {
            itemView.setOnClickListener {
                if (boundPosition != RecyclerView.NO_POSITION) {
                    onCardClick(boundPosition)
                }
            }
        }

        fun bind(card: Card, position: Int, size: Int) {
            boundPosition = position
            itemView.contentDescription =
                itemView.context.getString(R.string.card_description, position + 1)

            if (size > 0) {
                itemView.layoutParams = itemView.layoutParams.apply { height = size }
                cardInner.layoutParams =
                    (cardInner.layoutParams as FrameLayout.LayoutParams).apply {
                        width = size
                        height = size
                        gravity = Gravity.CENTER
                    }
            }

            cardInner.alpha = if (card.isMatched) 0.55f else 1f
            val showFace = card.isChosen || card.isMatched
            cardFace.visibility = if (showFace) View.VISIBLE else View.GONE
            cardBack.visibility = if (showFace) View.GONE else View.VISIBLE

            if (showFace) {
                cardFace.text = card.toString()
                cardFace.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        if (card.isRed) R.color.card_red else R.color.card_black
                    )
                )
                cardFace.setBackgroundResource(
                    if (card.isMatched) R.drawable.bg_card_matched
                    else R.drawable.bg_card_face
                )
            }
        }
    }
}
