package com.wilgig.vitrail.extensions

import com.wilgig.vitrail.config.GrammarSyntax.MODIFIER_OPERATOR
import com.wilgig.vitrail.config.GrammarSyntax.SYMBOL_END
import com.wilgig.vitrail.config.GrammarSyntax.SYMBOL_START

fun String.hasModifier(): Boolean = contains(MODIFIER_OPERATOR)
fun String.isNonTerminal(): Boolean = contains(SYMBOL_START) && contains(SYMBOL_END)
fun String.isTerminal(): Boolean = !isNonTerminal()
