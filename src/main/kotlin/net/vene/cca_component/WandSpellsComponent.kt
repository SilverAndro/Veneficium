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
import net.vene.common.item.ComponentItem
import net.vene.common.util.extension.getRawValue
import net.vene.data.StaticDataHandler
import net.vene.magic.spell_components.MagicEffect

class WandSpellsComponent : ComponentV3 {
    private var spells: MutableList<MagicEffect?> = mutableListOf()

    override fun readFromNbt(tag: CompoundTag) {
        // Iterates over all component lists in order
        next@ for (saved_component in tag.getList("spells", 8)) {
            saved_component as StringTag

            val componentName = saved_component.getRawValue()
            if (componentName == "!empty") {
                spells.add(null)
                continue@next
            }
            for (possible_component in VeneMain.ALL_COMPONENTS) {
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
            tagList.add(StringTag.of(spell?.name ?: "!empty"))
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
        fun get(stack: ItemStack): WandSpellsComponent {
            return VeneMain.WAND_SPELLS_COMPONENT.get(stack)
        }

        // Utility functions
        fun getSpellsFrom(stack: ItemStack): MutableList<MagicEffect?> {
            return VeneMain.WAND_SPELLS_COMPONENT.get(stack).spells
        }

        fun setSpells(stack: ItemStack, spells: MutableList<MagicEffect?>) {
            VeneMain.WAND_SPELLS_COMPONENT.get(stack).spells = spells
        }

        fun getComponentItems(stack: ItemStack): List<ComponentItem> {
            val out = mutableListOf<ComponentItem>()

            next@ for (saved_component in getSpellsFrom(stack)) {
                if (saved_component == null) {
                    continue@next
                }

                val componentName = saved_component.name
                if (componentName == "!empty") {
                    continue@next
                }

                for (possible_component in VeneMain.ALL_COMPONENTS) {
                    if (possible_component.name == componentName) {
                        try {
                            out.add(StaticDataHandler.spellComponent(possible_component.name) as ComponentItem)
                        } catch (ignored: Throwable) {}
                        finally {
                            continue@next
                        }
                    }
                }

                VeneMain.LOGGER.error("Unable to deserialize spell component $saved_component, skipping")
            }

            return out
        }
    }
}
