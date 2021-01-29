/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.vene.common.block.entity.SCCSBlockEntity
import net.vene.common.block.entity.render.SCCSBlockEntityRender
import net.vene.common.screen.WandEditScreen


class VeneMainClient : ClientModInitializer {
    override fun onInitializeClient() {
        // Screen
        ScreenRegistry.register(VeneMain.WAND_EDIT_SCREEN_HANDLER, ::WandEditScreen)

        // BERs
        BlockEntityRendererRegistry.INSTANCE.register(VeneMain.SCCS_BLOCK_ENTITY, ::SCCSBlockEntityRender)

        // Packets
        ClientPlayNetworking.registerGlobalReceiver(VeneMain.UPDATE_HELD_ITEM) { client: MinecraftClient, _: ClientPlayNetworkHandler, attachedData: PacketByteBuf, _: PacketSender ->
            val pos = attachedData.readBlockPos()
            val itemStack = attachedData.readItemStack()
            val isWorking = attachedData.readBoolean()
            client.execute {
                // Set the data on the client side
                try {
                    val blockEntity = client.world?.getBlockEntity(pos) as SCCSBlockEntity
                    blockEntity.heldItemStack = itemStack
                    blockEntity.isWorking = isWorking
                } catch(err: Throwable) {
                    // prob a desync, should be fixed soon
                }
            }
        }
    }
}
