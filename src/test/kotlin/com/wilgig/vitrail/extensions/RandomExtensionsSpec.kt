@file:Suppress("NAME_SHADOWING")
package com.wilgig.vitrail.extensions

import kotlin.random.Random
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object RandomExtensionsSpec : Spek({
    val list by memoized { listOf<String>() }

    describe("#elementFromList") {
        val random by memoized { Random(System.currentTimeMillis()) }
        context("with an empty list") {
            it("returns null") {
                assertEquals(null, random.elementFromList(list))
            }
        }

        context("with a non-empty list") {
            val list by memoized { listOf(0, 1, 2) }
            val random by memoized {
                val instance = Mockito.spy(Random(System.currentTimeMillis()))
                Mockito.doReturn(0).`when`(instance).nextInt(list.size)

                return@memoized instance
            }

            it("returns a random element from the list") {
                assertEquals(list.first(), random.elementFromList(list))
            }
        }
    }
})
