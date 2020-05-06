package com.wilgig.vitrail.modifiers

import org.junit.jupiter.api.Assertions.assertEquals
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object CapitalizeModifierSpec : Spek({
    val instance by memoized { CapitalizeModifier() }
    val string by memoized { "some string" }

    describe("#applyTo") {
        it("calls #capitalize on the given string and returns it") {
            assertEquals(string.capitalize(), instance.applyTo(string))
        }
    }
})
