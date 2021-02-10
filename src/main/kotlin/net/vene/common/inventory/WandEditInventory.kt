/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.inventory

import net.minecraft.inventory.SimpleInventory
import net.minecraft.screen.ScreenHandler

class WandEditInventory(private val handler: ScreenHandler) : SimpleInventory(10) {
    // Simple hack to get consistent content updates
    override fun markDirty() {
        super.markDirty()
        handler.onContentChanged(this)
    }
}
