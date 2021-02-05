/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.block.entity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.vene.VeneMain
import net.vene.client.screen.WandEditScreenHandler

class WandEditBlockEntity : BlockEntity(VeneMain.WAND_EDIT_BLOCK_ENTITY), NamedScreenHandlerFactory {
    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return WandEditScreenHandler(syncId, inv)
    }

    override fun getDisplayName(): Text {
        return TranslatableText("vene.screen.wand_edit.title")
    }
}
