package cn.edu.sicnu.cs.why.ex6

/** 第 2 个 Fragment：两张牌的翻牌游戏。 */
class TwoCardGameFragment : GameFragment() {

    override val cardsPerMatch = 2
    override val cardCountRes = R.integer.game2_card_count
    override val columnCountRes = R.integer.game2_card_columns
    override val titleRes = R.string.title_two_cards
    override val ruleRes = R.string.game_rule_two_cards
}
