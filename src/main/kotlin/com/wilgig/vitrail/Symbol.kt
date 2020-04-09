package com.wilgig.vitrail

import com.wilgig.vitrail.extensions.elementFromList
import kotlin.random.Random

class Symbol(
    private val rules: List<String>,
    private val random: Random = Random(System.currentTimeMillis())
) {
    init {
        if (rules.isEmpty()) throw IllegalArgumentException("Rule set should never be empty")
    }

    fun pickRule(): String = random.elementFromList(rules)!!
}