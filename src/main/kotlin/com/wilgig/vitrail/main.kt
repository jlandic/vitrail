package com.wilgig.vitrail

import kotlin.system.exitProcess

/**
 * Expands and prints a random expansion of the grammar described in `test.json`
 */
fun main() {
    val grammar = Grammar.fromJSON(
        Grammar::class.java.getResource("/test.json").readText()
    )

    for (i in 0 until 15) { println(grammar.flatten()) }
    exitProcess(0)
}
