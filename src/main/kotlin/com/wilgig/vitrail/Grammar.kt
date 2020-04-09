package com.wilgig.vitrail

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.wilgig.vitrail.config.GrammarSyntax.CAPTURE_END
import com.wilgig.vitrail.config.GrammarSyntax.CAPTURE_OPERATOR
import com.wilgig.vitrail.config.GrammarSyntax.CAPTURE_START
import com.wilgig.vitrail.config.GrammarSyntax.MODIFIER_OPERATOR
import com.wilgig.vitrail.config.GrammarSyntax.SYMBOL_END
import com.wilgig.vitrail.config.GrammarSyntax.SYMBOL_START
import com.wilgig.vitrail.extensions.hasModifier
import com.wilgig.vitrail.extensions.isNonTerminal
import com.wilgig.vitrail.extensions.isTerminal
import com.wilgig.vitrail.modifiers.Modifier
import kotlin.random.Random

class Grammar(
    private val symbols: MutableMap<String, Symbol> = hashMapOf(),
    private val modifiers: MutableMap<String, Modifier> = mutableMapOf(),
    private val random: Random = Random(System.currentTimeMillis())
) {
    companion object {
        const val DEFAULT_ROOT_KEY = "root"

        fun fromJSON(
            json: String,
            modifiers: MutableMap<String, Modifier> = mutableMapOf(),
            random: Random = Random(System.currentTimeMillis())
        ): Grammar {
            val mapper = ObjectMapper()
            val typeRef = object : TypeReference<Map<String, List<String>>>() {}
            val symbols: Map<String, List<String>> = mapper.readValue(json, typeRef)

            return Grammar(
                symbols.mapValues { Symbol(it.value, random) }.toMutableMap(),
                modifiers,
                random
            )
        }
    }

    fun withModifier(name: String, modifier: Modifier): Grammar {
        modifiers[name] = modifier
        return this
    }

    fun flatten(root: String = DEFAULT_ROOT_KEY): String = expand(getSymbol(root).pickRule())

    private fun expand(text: String): String {
        if (text.isTerminal()) return text

        var expandedText = text
        while (text.isNonTerminal()) {
            expandedText = expandText(text)
        }

        return expandedText
    }

    private fun expandText(text: String): String {
        var symbolStartIndex = 0
        var captureStartIndex = 0

        for (i in text.indices) {
            when (text[i]) {
                SYMBOL_START -> symbolStartIndex = i + 1
                CAPTURE_START -> captureStartIndex = i + 1
                SYMBOL_END -> {
                    val key = text.substring(symbolStartIndex until i)
                    val expandedText = expandSymbol(key)
                    return text.replaceRange(symbolStartIndex - 1, i + 1, expandedText)
                }
                CAPTURE_END -> {
                    val capture = text.substring(captureStartIndex until i)
                    captureSymbol(capture)
                    return text.replaceRange(captureStartIndex - 1, i + 1, "")
                }
            }
        }

        return text
    }

    private fun expandSymbol(text: String): String {
        val key = text.substringBefore(MODIFIER_OPERATOR)
        val symbol = getSymbol(key)
        var expandedText = expandText(symbol.pickRule())

        if (text.hasModifier()) {
            expandedText = applyModifiers(expandedText, text.substringAfter(MODIFIER_OPERATOR).split(MODIFIER_OPERATOR))
        }

        return expandedText
    }

    private fun applyModifiers(text: String, modifierNames: List<String>): String {
        var modifiedText = text
        modifierNames.mapNotNull {
            modifiers[it]
        }.forEach {
            modifiedText = it.apply(modifiedText)
        }

        return modifiedText
    }

    private fun captureSymbol(text: String) {
        val capture = text.split(CAPTURE_OPERATOR)

        if (capture.size != 2) throw error("Invalid capture: `$text`")

        val newSymbol = capture[1]
        val extrapolationKey = capture[0]
        symbols[newSymbol] = Symbol(listOf(getSymbol(extrapolationKey).pickRule()), random)
    }

    private fun getSymbol(key: String) = symbols[key] ?: error("Symbol $key does not lead to any other valid rule")
}