package com.wilgig.vitrail

fun main() {
    val grammar = Grammar.fromJSON(
        Grammar::class.java.getResource("/test.json").readText()
    )

    println(grammar.flatten())
}