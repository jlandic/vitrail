package com.wilgig.vitrail.config

abstract class GrammarSyntax {
    abstract val symbolStart: Char
    abstract val symbolEnd: Char
    abstract val captureStart: Char
    abstract val captureEnd: Char
    abstract val captureOperator: Char
    abstract val modifierOperator: Char
}