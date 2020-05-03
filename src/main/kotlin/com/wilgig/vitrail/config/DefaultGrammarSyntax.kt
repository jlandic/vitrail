package com.wilgig.vitrail.config

/**
 * Describes how `Grammar` interprets the grammar syntax by default, if
 * no other custom `GrammarSyntax` instance is implemented.
 *
 * @see GrammarSyntax
 * @author @jlandic
 */
object DefaultGrammarSyntax : GrammarSyntax() {
    override val symbolStart: Char = '{'
    override val symbolEnd: Char = '}'
    override val captureStart: Char = '['
    override val captureEnd: Char = ']'
    override val captureOperator: Char = '>'
    override val modifierOperator: Char = ':'
}
