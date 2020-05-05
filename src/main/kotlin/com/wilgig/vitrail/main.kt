package com.wilgig.vitrail

import kotlin.system.exitProcess

/**
 * Expands and prints a random expansion of the grammar described in `test.json`
 */
fun main() {
    val grammar = Grammar.fromJSON(
        Grammar::class.java.getResource("/test.json").readText()
    )

    println(grammar.flatten())
    exitProcess(0)
}
