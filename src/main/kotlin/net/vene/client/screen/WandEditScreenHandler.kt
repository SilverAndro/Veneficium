/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.client.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.vene.VeneMain
import net.vene.cca_component.WandSpellsComponent
import net.vene.common.inventory.WandEditInventory
import net.vene.common.item.ComponentItem
import net.vene.magic.spell_components.MagicEffect
import net.vene.client.screen.slot.ComponentOnlySlot
import net.vene.client.screen.slot.WandOnlySlot
import net.vene.common.item.casting.SpellProvider


class WandEditScreenHandler(syncId: Int, playerInventory: PlayerInventory) : ScreenHandler(VeneMain.WAND_EDIT_SCREEN_HANDLER, syncId) {
    private val inventory: Inventory = WandEditInventory(this)
    private var lastKnownInventory = SimpleInventory(10)

    private var ignoreContentUpdates = false

    override fun canUse(player: PlayerEntity): Boolean {
        return inventory.canPlayerUse(player)
    }

    // Shift + Player Inv Slot
    override fun transferSlot(player: PlayerEntity, invSlot: Int): ItemStack {
        var newStack = ItemStack.EMPTY
        val slot = slots[invSlot]
        if (slot != null && slot.hasStack()) {
            val originalStack = slot.stack
            newStack = originalStack.copy()
            if (invSlot < inventory.size()) {
                if (!insertItem(originalStack, inventory.size(), slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(originalStack, 0, inventory.size(), false)) {
                return ItemStack.EMPTY
            }
            if (originalStack.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return newStack
    }

    init {
        checkSize(inventory, 10)
        inventory.onOpen(playerInventory.player)

        // Slots for spell components
        for (m in 0 until 9) {
            addSlot(ComponentOnlySlot(inventory, m + 1, (17 / 2) + (m * 18), 17))
        }

        // Slot of wand
        addSlot(WandOnlySlot(inventory, 0, (17 / 2) + (4 * 18), 17 * 3))

        //The player inventory
        for (m in 0 until 3) {
            for (l in 0 until 9) {
                addSlot(Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18))
            }
        }

        //The player Hotbar
        for (m in 0 until 9) {
            addSlot(Slot(playerInventory, m, 8 + m * 18, 142))
        }
    }

    override fun onContentChanged(inventory: Inventory) {
        val wand = inventory.getStack(0)
        val spells: MutableList<MagicEffect?> = mutableListOf()

        super.onContentChanged(inventory)

        val oldWand = lastKnownInventory.getStack(0)

        // Ignore known changes, we only really care about the player inserting/removing stuff
        if (ignoreContentUpdates) {
            return
        }

        // Wands swapped
        if (oldWand.item is SpellProvider && wand.item is SpellProvider && WandSpellsComponent.get(oldWand) != WandSpellsComponent.get(wand)) {
            // Save old spells to old wand
            for (i in 1 until 10) {
                if (inventory.getStack(i).item is ComponentItem) {
                    spells.add((inventory.getStack(i).item as ComponentItem).effect)
                } else {
                    spells.add(null)
                }
                WandSpellsComponent.setSpells(oldWand, spells)
            }

            // Unpack new spells
            // Logic for unpacking wand spells
            ignoreContentUpdates = true
            for (i in 1 until 10) {
                inventory.setStack(i, ItemStack.EMPTY)
            }
            WandSpellsComponent.getSpellsFrom(wand).forEachIndexed { found: Int, wantedEffect ->
                VeneMain.SPELL_COMPONENT_ITEMS.values.forEach {
                    if (it.effect == wantedEffect) {
                        inventory.setStack(found + 1, ItemStack(it))
                    }
                }
            }
            ignoreContentUpdates = false
        }

        // Wand stayed in
        if (oldWand.item is SpellProvider && wand.item is SpellProvider && WandSpellsComponent.get(oldWand) == WandSpellsComponent.get(wand)) {
            for (i in 1 until 10) {
                if (inventory.getStack(i).item is ComponentItem) {
                    spells.add((inventory.getStack(i).item as ComponentItem).effect)
                } else {
                    spells.add(null)
                }
                WandSpellsComponent.setSpells(wand, spells)
            }
        }

        // Wand taken out
        if (oldWand.item is SpellProvider && wand.item !is SpellProvider) {
            ignoreContentUpdates = true
            inventory.clear()
            ignoreContentUpdates = false

            slots.forEach {
                if (it is ComponentOnlySlot) {
                    it.active = false
                }
            }
        }

        // Wand put in
        if (oldWand.item !is SpellProvider && wand.item is SpellProvider) {
            slots.forEach {
                if (it is ComponentOnlySlot) {
                    it.active = true
                }
            }
            // Logic for unpacking wand spells
            ignoreContentUpdates = true
            WandSpellsComponent.getSpellsFrom(wand).forEachIndexed { found: Int, wantedEffect ->
                VeneMain.SPELL_COMPONENT_ITEMS.values.forEach {
                    if (it.effect == wantedEffect) {
                        inventory.setStack(found + 1, ItemStack(it))
                    }
                }
            }
            ignoreContentUpdates = false
        }

        // Basically a deep copy because I kept having issues with lastKnownInventory syncing with inventory
        for (x in 0 until 10) {
            lastKnownInventory.setStack(x, inventory.getStack(x).copy())
        }
    }

    override fun close(player: PlayerEntity) {
        val toDrop = SimpleInventory(1)
        toDrop.setStack(0, inventory.getStack(0))
        dropInventory(player, player.world, toDrop)
    }
}
