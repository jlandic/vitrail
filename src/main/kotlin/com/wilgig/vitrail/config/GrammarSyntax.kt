package com.wilgig.vitrail.config

/**
 * Describes how `Grammar` interprets the grammar it expands, in terms of operators and syntax:
 *
 * - What determines a variable capture
 * - What determines a symbol to be expanded
 * - How are modifiers called
 * - ...
 *
 * By default, `Grammar` uses the syntax implemented by `DefaultGrammarSyntax`.
 *
 * Implement `GrammarSyntax` to use your own grammar syntax.
 *
 * @property symbolStart character starting a symbol expression
 * @property symbolEnd character ending a symbol expression
 * @property captureStart character starting a capture expression
 * @property captureEnd character ending a capture expression
 * @property captureOperator character separating the symbol used to generate the value of the capture, and the name of the variable holding this value
 * @property modifierOperator character separating the symbol, and the modifier(s) applied to its expanded value
 *
 * @see DefaultGrammarSyntax
 * @author @jlandic
 */
abstract class GrammarSyntax {
    abstract val symbolStart: Char
    abstract val symbolEnd: Char
    abstract val captureStart: Char
    abstract val captureEnd: Char
    abstract val captureOperator: Char
    abstract val modifierOperator: Char
}
