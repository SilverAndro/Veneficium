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
import net.vene.client.screen.WandEditScreen
import net.fabricmc.fabric.api.`object`.builder.v1.client.model.FabricModelPredicateProviderRegistry
import net.minecraft.client.item.ModelPredicateProvider
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.vene.common.item.casting.MagicCrossbow


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

        // Item renderers
        FabricModelPredicateProviderRegistry.register(
            Identifier("pull")
        ) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity? ->
            return@register if (entity == null) { 0.0f } else {
                if (entity.activeItem.item !== VeneMain.MAGIC_CROSSBOW_ITEM) {
                    0.0f
                } else {
                    (CrossbowItem.getPullTime(stack) - entity.itemUseTimeLeft) / CrossbowItem.getPullTime(stack).toFloat()
                }
            }
        }

        FabricModelPredicateProviderRegistry.register(Identifier("pulling")) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity? ->
            if (entity != null && entity.isUsingItem && entity.activeItem == stack) 1.0f else 0.0f
        }

        FabricModelPredicateProviderRegistry.register(Identifier("charged")) { stack: ItemStack, _: ClientWorld?, entity: LivingEntity? ->
            if (entity != null && CrossbowItem.isCharged(stack)) 1.0f else 0.0f
        }
    }
}
