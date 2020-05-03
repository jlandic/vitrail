package com.wilgig.vitrail

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.random.Random

object SymbolSpec : Spek({
    describe("#init") {
        context("when rules is empty") {
            val rules by memoized { mutableListOf<String>() }

            it("throws an IllegalArgumentException exception") {
                assertThrows<IllegalArgumentException> { Symbol(rules) }
            }
        }
    }

    describe("#pickRule") {
        val rules by memoized { mutableListOf("ruleA") }
        val random by memoized {
            val instance = Mockito.spy(Random(System.currentTimeMillis()))
            Mockito.doReturn(0).`when`(instance).nextInt(rules.size)

            return@memoized instance
        }
        val symbol by memoized { Symbol(rules, random) }

        it("returns a random rule from rules") {
            assertEquals(rules.first(), symbol.pickRule())
        }
    }

    describe("#addRule") {
        val rules by memoized { mutableListOf("ruleA") }
        val symbol by memoized { Symbol(rules) }
        val newRule by memoized { "new rule" }

        it ("returns the symbol itself") {
            assertEquals(symbol, symbol.addRule(newRule))
        }

        it("adds the rule to rules") {
            symbol.addRule(newRule)
            val field = Symbol::class.java.getDeclaredField("rules")
            field.trySetAccessible()
            assertTrue((field.get(symbol) as MutableList<String>).contains(newRule))
        }
    }
})