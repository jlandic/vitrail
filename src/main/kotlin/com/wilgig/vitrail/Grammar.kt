package com.wilgig.vitrail

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.wilgig.vitrail.config.DefaultGrammarSyntax
import com.wilgig.vitrail.config.GrammarSyntax
import com.wilgig.vitrail.extensions.hasModifier
import com.wilgig.vitrail.extensions.isNonTerminal
import com.wilgig.vitrail.extensions.isTerminal
import com.wilgig.vitrail.modifiers.Modifier
import kotlin.random.Random

class Grammar(
    private val symbols: MutableMap<String, Symbol> = hashMapOf(),
    private val modifiers: MutableMap<String, Modifier> = mutableMapOf(),
    private val random: Random = Random(System.currentTimeMillis()),
    private val syntax: GrammarSyntax = DefaultGrammarSyntax
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
                symbols.mapValues { Symbol(it.value.toMutableList(), random) }.toMutableMap(),
                modifiers,
                random
            )
        }
    }

    fun withSymbol(symbol: String, rules: List<String>): Grammar {
        symbols[symbol] = Symbol(rules.toMutableList(), random)
        return this
    }

    fun withRule(symbol: String, rule: String): Grammar {
        if (symbols.containsKey(symbol)) {
            symbols[symbol]!!.addRule(rule)
        } else {
            throw error("Cannot add rule to non-existing symbol `$symbol`")
        }

        return this
    }

    fun withModifier(name: String, modifier: Modifier): Grammar {
        modifiers[name] = modifier
        return this
    }

    fun flatten(root: String = DEFAULT_ROOT_KEY): String = expand(getSymbol(root).pickRule())

    private fun expand(text: String): String {
        if (text.isTerminal(syntax)) return text

        var expandedText = text
        while (expandedText.isNonTerminal(syntax)) {
            expandedText = expandText(expandedText)
        }

        return expandedText
    }

    private fun expandText(text: String): String {
        var symbolStartIndex = 0
        var captureStartIndex = 0

        for (i in text.indices) {
            when (text[i]) {
                syntax.symbolStart -> symbolStartIndex = i + 1
                syntax.captureStart -> captureStartIndex = i + 1
                syntax.symbolEnd -> {
                    val key = text.substring(symbolStartIndex until i)
                    val expandedText = expandSymbol(key)
                    return text.replaceRange(symbolStartIndex - 1, i + 1, expandedText)
                }
                syntax.captureEnd -> {
                    val capture = text.substring(captureStartIndex until i)
                    captureSymbol(capture)
                    return text.replaceRange(captureStartIndex - 1, i + 1, "")
                }
            }
        }

        return text
    }

    private fun expandSymbol(text: String): String {
        val key = text.substringBefore(syntax.modifierOperator)
        val symbol = getSymbol(key)
        var expandedText = expandText(symbol.pickRule())

        if (text.hasModifier(syntax)) {
            expandedText = applyModifiers(
                expandedText,
                text.substringAfter(syntax.modifierOperator).split(syntax.modifierOperator)
            )
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
        val capture = text.split(syntax.captureOperator)

        if (capture.size != 2) throw error("Invalid capture: `$text`")

        val newSymbol = capture[1]
        val extrapolationKey = capture[0]
        symbols[newSymbol] = Symbol(mutableListOf(getSymbol(extrapolationKey).pickRule()), random)
    }

    private fun getSymbol(key: String) = symbols[key] ?: error("Symbol $key does not lead to any other valid rule")
}
