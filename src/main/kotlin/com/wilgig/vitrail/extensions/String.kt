package com.wilgig.vitrail.extensions

import com.wilgig.vitrail.config.GrammarSyntax

fun String.hasModifier(syntax: GrammarSyntax): Boolean = contains(syntax.modifierOperator)
fun String.isNonTerminal(syntax: GrammarSyntax): Boolean = contains(syntax.symbolStart) && contains(syntax.symbolEnd)
fun String.isTerminal(syntax: GrammarSyntax): Boolean = !isNonTerminal(syntax)
