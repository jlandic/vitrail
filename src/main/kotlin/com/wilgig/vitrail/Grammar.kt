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

/**
 * Reads a context-free grammar description, based on a given customisable syntax, and expands the grammar from a given root symbol.
 *
 * @param symbols maps symbol names to their rules ([Symbol]), as described by the grammar
 * @param modifiers maps modifier names (as used in the grammar definition) to the corresponding [Modifier] class describing their logic
 * @param random instance of [Random] used to pick rules when expanding symbols
 * @param syntax the syntax used to describe the grammar
 *
 * @see Symbol
 * @see GrammarSyntax
 * @see DefaultGrammarSyntax
 * @see Modifier
 *
 * @author @jlandic
 */
class Grammar(
    val symbols: MutableMap<String, Symbol> = hashMapOf(),
    private val modifiers: MutableMap<String, Modifier> = mutableMapOf(),
    private val random: Random = Random(System.currentTimeMillis()),
    private val syntax: GrammarSyntax = DefaultGrammarSyntax
) {
    companion object {
        /**
         * By default, the grammar will look for a `root` symbol, and start expanding from there
         */
        const val DEFAULT_ROOT_KEY = "root"

        /**
         * Create a [Grammar] instance from a grammar described in a JSON file
         *
         * @param json the path to the json file to parse
         * @param modifiers maps modifier names (as used in the grammar definition) to the corresponding [Modifier] class describing their logic
         * @param random instance of [Random] used to pick rules when expanding symbols
         * @param syntax the syntax used to describe the grammar
         */
        fun fromJSON(
            json: String,
            modifiers: MutableMap<String, Modifier> = mutableMapOf(),
            random: Random = Random(System.currentTimeMillis()),
            syntax: GrammarSyntax = DefaultGrammarSyntax
        ): Grammar {
            val mapper = ObjectMapper()
            val typeRef = object : TypeReference<Map<String, List<String>>>() {}
            val symbols: Map<String, List<String>> = mapper.readValue(json, typeRef)

            return Grammar(
                symbols.mapValues { Symbol(it.value.toMutableList(), random) }.toMutableMap(),
                modifiers,
                random,
                syntax
            )
        }
    }

    /**
     * Dynamically add a symbol with rules to the grammar.
     *
     * The method returns the modified grammar instance, so you can build upon it.
     *
     * @param symbol the symbol name
     * @param rules the list of possible expansions
     *
     * @return the grammar instance itself
     */
    fun withSymbol(symbol: String, rules: List<String>): Grammar {
        symbols[symbol] = Symbol(rules.toMutableList(), random)
        return this
    }

    /**
     * Dynamically add a rule to an existing symbol.
     *
     * The method returns the modified grammar instance, so you can build upon it.
     *
     * @param symbol the symbol name
     * @param rule the rule to be added
     *
     * @return the grammar instance itself
     * @throws IllegalArgumentException if `symbol` does not exist
     */
    fun withRule(symbol: String, rule: String): Grammar {
        if (symbols.containsKey(symbol)) {
            symbols[symbol]!!.addRule(rule)
        } else {
            throw IllegalArgumentException("Cannot add rule to non-existing symbol `$symbol`")
        }

        return this
    }

    /**
     * Dynamically add a modifier to the grammar.
     *
     * The method returns the modified grammar instance, so you can build upon it.
     *
     * @param name the modifier name, used to invoke it on a symbol inside the grammar
     * @param modifier the [Modifier] instance to apply to the symbol when the modifier is called
     * @return the grammar instance itself
     */
    fun withModifier(name: String, modifier: Modifier): Grammar {
        modifiers[name] = modifier
        return this
    }

    /**
     * Expand the whole grammar from the given root symbol, until it reaches all terminal symbols, and returns the flattened result, as a single String.
     *
     * @param root the symbol to start expanding from
     * @return the resulting string from the expansion of all symbols
     */
    fun flatten(root: String = DEFAULT_ROOT_KEY): String = expand(getSymbol(root).pickRule())

    /**
     * @param key the key to the symbol rules
     * @return the [Symbol] instance mapped to the given key
     * @throws IllegalArgumentException if the key does not exist in the Grammar
     */
    fun getSymbol(key: String) =
        symbols[key] ?: throw IllegalArgumentException("Symbol $key does not lead to any other valid rule")

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
            modifiedText = it.applyTo(modifiedText)
        }

        return modifiedText
    }

    private fun captureSymbol(text: String) {
        val capture = text.split(syntax.captureOperator)

        if (capture.size != 2) throw error("Invalid capture: `$text`")

        val newSymbol = capture[1]
        val extrapolationKey = capture[0]
        withSymbol(newSymbol, mutableListOf(getSymbol(extrapolationKey).pickRule()))
    }
}
