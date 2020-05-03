package com.wilgig.vitrail

import com.wilgig.vitrail.extensions.elementFromList
import kotlin.random.Random

class Symbol(
    private val rules: MutableList<String>,
    private val random: Random = Random(System.currentTimeMillis())
) {
    init {
        if (rules.isEmpty()) throw IllegalArgumentException("Rule set should never be empty")
    }

    fun addRule(rule: String): Symbol {
        rules.add(rule)
        return this
    }

    fun pickRule(): String = random.elementFromList(rules)!!
}
