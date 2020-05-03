package com.wilgig.vitrail.extensions

import com.wilgig.vitrail.config.GrammarSyntax

/**
 * @param syntax the grammar syntax that determines the characters used as operators
 * @return whether the string includes modifiers
 * @author @jlandic
 */
fun String.hasModifier(syntax: GrammarSyntax): Boolean = contains(syntax.modifierOperator)

/**
 * @param syntax the grammar syntax that determines the characters used as operators
 * @return whether the symbol is non-terminal (it includes symbols that need to be expanded further)
 * @author @jlandic
 */
fun String.isNonTerminal(syntax: GrammarSyntax): Boolean = contains(syntax.symbolStart) && contains(syntax.symbolEnd)

/**
 * @param syntax the grammar syntax that determines the characters used as operators
 * @return whether the symbol is terminal (it does not include symbols that need to be expanded)
 * @author @jlandic
 */
fun String.isTerminal(syntax: GrammarSyntax): Boolean = !isNonTerminal(syntax)
