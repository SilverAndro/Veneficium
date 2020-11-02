/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.screen.slot

import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.vene.common.item.ComponentItem

// Only allows spell components (blank is NOT a component)
class ComponentOnlySlot(inventory: Inventory, index: Int, x: Int, y: Int) : Slot(inventory, index, x, y) {
    var active = false

    override fun canInsert(stack: ItemStack?): Boolean {
        if (stack != null && active) {
            return stack.item is ComponentItem
        }
        return false
    }

    override fun doDrawHoveringEffect(): Boolean {
        return active
    }
}