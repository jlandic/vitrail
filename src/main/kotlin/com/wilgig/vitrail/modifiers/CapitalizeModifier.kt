package com.wilgig.vitrail.modifiers

class CapitalizeModifier : Modifier {
    override fun applyTo(sourceText: String) = sourceText.capitalize()
}
