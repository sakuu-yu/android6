package cn.edu.sicnu.cs.why.ex6

/** 第 3 个 Fragment：三张牌的翻牌游戏。 */
class ThreeCardGameFragment : GameFragment() {

    override val cardsPerMatch = 3
    override val cardCountRes = R.integer.game3_card_count
    override val columnCountRes = R.integer.game3_card_columns
    override val titleRes = R.string.title_three_cards
    override val ruleRes = R.string.game_rule_three_cards
}
