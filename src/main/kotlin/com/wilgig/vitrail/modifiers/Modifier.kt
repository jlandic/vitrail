package com.wilgig.vitrail.modifiers

interface Modifier {
    fun apply(sourceText: String): String
}