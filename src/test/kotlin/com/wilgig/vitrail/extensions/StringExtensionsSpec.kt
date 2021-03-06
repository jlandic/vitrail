package com.wilgig.vitrail.extensions

import com.wilgig.vitrail.config.DefaultGrammarSyntax
import com.wilgig.vitrail.config.DefaultGrammarSyntax.symbolEnd
import com.wilgig.vitrail.config.DefaultGrammarSyntax.symbolStart
import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object StringExtensionsSpec : Spek({
    val syntax by memoized { DefaultGrammarSyntax }

    describe("#hasModifier") {
        context("when string contains the modifier operator") {
            val operator by memoized { syntax.modifierOperator }
            val string by memoized { "something $operator something" }

            it("returns true") {
                assertEquals(true, string.hasModifier(syntax))
            }
        }

        context("when string does not contain the modifier operator") {
            val string by memoized { "no operator present" }

            it("returns false") {
                assertEquals(false, string.hasModifier(syntax))
            }
        }
    }

    describe("#isTerminal") {
        context("when string has both a symbol start and symbol end character") {
            val start by memoized { syntax.symbolStart }
            val end by memoized { syntax.symbolEnd }
            val string by memoized { "Something $start symbol name $end something" }

            it("returns false") {
                assertEquals(false, string.isTerminal(syntax))
            }
        }

        context("when string has neither a symbol start nor end character") {
            val string by memoized { "Something else" }

            it("returns true") {
                assertEquals(true, string.isTerminal(syntax))
            }
        }

        context("when string has a symbol start but no symbol end character") {
            val string by memoized { "Something $symbolStart else" }

            it("returns false") {
                assertEquals(true, string.isTerminal(syntax))
            }
        }

        context("when string has a symbol end but no symbol start character") {
            val string by memoized { "Something else$symbolEnd" }

            it("returns false") {
                assertEquals(true, string.isTerminal(syntax))
            }
        }
    }

    describe("#isNonTerminal") {
        context("when string has both a symbol start and symbol end character") {
            val start by memoized { syntax.symbolStart }
            val end by memoized { syntax.symbolEnd }
            val string by memoized { "Something $start symbol name $end something" }

            it("returns true") {
                assertEquals(true, string.isNonTerminal(syntax))
            }
        }

        context("when string does not have both a symbol start and symbol end character") {
            val string by memoized { "Something else" }

            it("returns false") {
                assertEquals(false, string.isNonTerminal(syntax))
            }
        }
    }
})
