package net.vene.common.item.casting

import net.minecraft.item.Item

class InfusedStick(settings: Settings) : Item(settings), SpellProvider {
    override fun getMaxSpells(): Int {
        return 5
    }
}
