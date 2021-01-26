package net.vene.magic.spell_components.types

import net.vene.VeneMain
import net.vene.magic.spell_components.MagicEffect
import net.vene.magic.spell_components.SpellRunnable

class CosmeticComponent(override val name: String, spellMethod: SpellRunnable) : MagicEffect(name, spellMethod) {
    override val type: ComponentType = ComponentType.COSMETIC

    override fun toString(): String {
        return "CosmeticEffect[$name]"
    }

    init {
        VeneMain.COSMETIC_COMPONENTS.add(this)
    }
}
