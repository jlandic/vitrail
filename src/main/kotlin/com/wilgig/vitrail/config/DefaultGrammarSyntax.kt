package com.wilgig.vitrail.config

object DefaultGrammarSyntax : GrammarSyntax() {
    override val symbolStart: Char = '{'
    override val symbolEnd: Char = '}'
    override val captureStart: Char = '['
    override val captureEnd: Char = ']'
    override val captureOperator: Char = '>'
    override val modifierOperator: Char = ':'
}
