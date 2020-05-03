package com.wilgig.vitrail

import com.wilgig.vitrail.extensions.elementFromList
import kotlin.random.Random

/**
 * Symbol defined as a list of possible rules it can return when expanded.
 *
 * @property rules list of possible rules
 * @property random instance of [Random] used to pick random rules from the list when expanding the symbol
 * @constructor throws [IllegalArgumentException] if the initial rule set is empty
 *
 * @author @jlandic
 */
class Symbol(
    private val rules: MutableList<String>,
    private val random: Random = Random(System.currentTimeMillis())
) {
    init {
        if (rules.isEmpty()) throw IllegalArgumentException("Rule set should never be empty")
    }

    /**
     * Dynamically add a rule to the symbol.
     *
     * @param rule the rule to add
     * @return the symbol instance itself
     */
    fun addRule(rule: String): Symbol {
        rules.add(rule)
        return this
    }

    /**
     * @return a random rule from the rule set
     */
    fun pickRule(): String = random.elementFromList(rules)!!
}
