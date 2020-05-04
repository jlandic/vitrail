package com.wilgig.vitrail

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GrammarSpec : Spek({
    val instance by memoized { Grammar() }

    describe("#withSymbol") {
        val symbol by memoized { "root" }
        val rules by memoized { listOf("rule") }

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
        val rule by memoized { "newRule" }
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
            val symbol by memoized { "nonExisting" }

            it("throws IllegalArgumentException") {
                assertThrows<IllegalArgumentException> { instance.withRule(symbol, rule) }
            }
        }
    }

    describe("#flatten") {
        val root by memoized { "root" }
        val uniqueRule by memoized { "rule" }
        val rules by memoized { mutableListOf(uniqueRule) }
        val symbol by memoized { Symbol(rules) }
        val symbols by memoized { mutableMapOf(root to symbol) }
        val instance by memoized { Grammar(symbols = symbols) }

        it("expands a random rule from the given root symbol") {
            assertEquals(uniqueRule, instance.flatten(root))
        }
    }
})
