package com.wilgig.vitrail

import kotlin.system.exitProcess

fun main() {
    val grammar = Grammar.fromJSON(
        Grammar::class.java.getResource("/test.json").readText()
    )

    println(grammar.flatten())
    exitProcess(0)
}