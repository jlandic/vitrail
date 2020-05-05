package com.wilgig.vitrail

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.spekframework.spek2.Spek
import org.spekframework.spek2.dsl.Root
import org.spekframework.spek2.style.specification.describe

fun Root.setup() {
    val root by memoized { "root" }
    val rule by memoized { "rule" }
    val rules by memoized { mutableListOf(rule) }
    val symbol by memoized { Symbol(rules) }
    val symbols by memoized { mutableMapOf(root to symbol) }

    @Suppress("UNUSED_VARIABLE")
    val instance by memoized { Grammar(symbols = symbols) }
}

object GrammarSpec : Spek({
    setup()

    describe("#withSymbol") {
        val symbol: String by memoized { "new symbol" }
        val rules: MutableList<String> by memoized { mutableListOf("rule") }
        val instance: Grammar by memoized()

        it("returns the Grammar instance itself") {
            assertEquals(instance, instance.withSymbol(symbol, rules))
        }

        it("adds a new symbol with the given rules to the grammar") {
            instance.withSymbol(symbol, rules)
            assertEquals(rules, instance.symbols[symbol]!!.rules)
        }
    }

    describe("#withRule") {
        val symbol by memoized { "root" }
        val rule: String by memoized()
        val initialRule by memoized { "initialRule" }
        val initialRules by memoized { mutableMapOf(symbol to Symbol(mutableListOf(initialRule))) }
        val instance by memoized { Grammar(initialRules) }

        context("with an already existing symbol") {
            it("returns the Grammar instance itself") {
                assertEquals(instance, instance.withRule(symbol, rule))
            }

            it("adds the rule to the rules existing for the symbol") {
                instance.withRule(symbol, rule)
                assertEquals(listOf(initialRule, rule), instance.symbols[symbol]!!.rules)
            }
        }

        context("with a non-existing symbol") {
            val nonExistingSymbol by memoized { "nonExisting" }

            it("throws IllegalArgumentException") {
                assertThrows<IllegalArgumentException> { instance.withRule(nonExistingSymbol, rule) }
            }
        }
    }

    describe("#flatten") {
        val root: String by memoized()
        val rule: String by memoized()
        val instance: Grammar by memoized()

        it("expands a random rule from the given root symbol") {
            assertEquals(rule, instance.flatten(root))
        }
    }

    describe("#getSymbol") {
        val root: String by memoized()
        val symbol: Symbol by memoized()
        val instance: Grammar by memoized()

        context("with a non-existing symbol key") {
            it("throws an error") {

                assertThrows<IllegalArgumentException> { instance.getSymbol("non-existing") }
            }
        }

        context("with an existing symbol key") {
            it("returns the associated symbol instance") {
                assertEquals(symbol, instance.getSymbol(root))
            }
        }
    }
})
