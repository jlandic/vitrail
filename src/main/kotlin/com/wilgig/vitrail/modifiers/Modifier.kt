package com.wilgig.vitrail.modifiers

/**
 * Describes a modification applied on a given expanded symbol (String)
 *
 * @author @jlandic
 */
interface Modifier {
    /**
     * Apply the modifier's logic to the given string, and return the result
     *
     * @param sourceText the expanded symbol to modify
     * @return the modified string
     */
    fun applyTo(sourceText: String): String
}
