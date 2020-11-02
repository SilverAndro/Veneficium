/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.cca_component

import dev.onyxstudios.cca.api.v3.component.ComponentV3
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.vene.VeneMain
import net.vene.access.StringValueAccessor
import net.vene.common.magic.spell_components.MagicEffect

class WandSpellsComponent : ComponentV3 {
    private var spells: MutableList<MagicEffect> = mutableListOf()

    override fun readFromNbt(tag: CompoundTag) {
        // Iterates over all component lists in order
        // TODO: Replace with "union" keyword to compress
        next@ for (saved_component in tag.getList("spells", 8)) {
            saved_component as StringTag

            val componentName = (saved_component as StringValueAccessor).value
            for (possible_component in VeneMain.RESULT_COMPONENTS) {
                if (possible_component.name == componentName) {
                    spells.add(possible_component)
                    continue@next
                }
            }

            for (possible_component in VeneMain.MOVE_COMPONENTS) {
                if (possible_component.name == componentName) {
                    spells.add(possible_component)
                    continue@next
                }
            }

            for (possible_component in VeneMain.MATERIAL_COMPONENTS) {
                if (possible_component.name == componentName) {
                    spells.add(possible_component)
                    continue@next
                }
            }

            VeneMain.LOGGER.error("Unable to deserialize spell component $saved_component, skipping")
        }
    }

    override fun writeToNbt(tag: CompoundTag) {
        val tagList = ListTag()
        for (spell in spells) {
            tagList.add(StringTag.of(spell.name))
        }
        tag.put("spells", tagList)
    }

    override fun equals(other: Any?): Boolean {
        if (other is WandSpellsComponent) {
            return this.spells == other.spells
        }
        return false
    }

    override fun hashCode(): Int {
        return spells.hashCode()
    }

    companion object {
        // Utility functions
        fun getSpellsFrom(stack: ItemStack): MutableList<MagicEffect> {
            return VeneMain.WAND_SPELLS_COMPONENT.get(stack).spells
        }

        fun setSpells(stack: ItemStack, spells: MutableList<MagicEffect>) {
            VeneMain.WAND_SPELLS_COMPONENT.get(stack).spells = spells
        }
    }
}