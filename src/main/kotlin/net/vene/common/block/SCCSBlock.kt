/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.block

import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.vene.common.block.entity.SCCSBlockEntity


class SCCSBlock(settings: Settings) : BlockWithEntity(settings) {
    override fun createBlockEntity(world: BlockView): BlockEntity {
        return SCCSBlockEntity()
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // Don't do anything on the client
        if (world.isClient) {
            return ActionResult.SUCCESS
        }

        // For quick reference later
        val itemUsed = player.getStackInHand(hand)
        val blockEntity = world.getBlockEntity(pos) as SCCSBlockEntity

        // Don't mess with items if its locked
        if (blockEntity.lockItem) {
            return ActionResult.FAIL
        }

        // Take the item out (empty hand)
        if (itemUsed.item == Items.AIR && blockEntity.heldItemStack.item != Items.AIR) {
            player.giveItemStack(blockEntity.heldItemStack.copy())
            blockEntity.heldItemStack = ItemStack.EMPTY

            // Play some sounds
            (player as ServerPlayerEntity).networkHandler.sendPacket(PlaySoundIdS2CPacket(
                    SoundEvents.BLOCK_ENDER_CHEST_OPEN.id,
                    SoundCategory.BLOCKS,
                    Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5),
                    0.3F,
                    2.8F)
            )
            player.networkHandler.sendPacket(PlaySoundIdS2CPacket(
                    SoundEvents.BLOCK_END_PORTAL_FRAME_FILL.id,
                    SoundCategory.BLOCKS,
                    Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5),
                    0.5F,
                    3.8F)
            )
        } else {
            // Not an empty hand
            if (itemUsed.item != Items.AIR) {
                // Drop the existing held item
                if (blockEntity.heldItemStack.item != Items.AIR) {
                    ItemScatterer.spawn(world, pos.x + 0.5, pos.y + 1.1, pos.z + 0.5, blockEntity.heldItemStack)
                }

                // Take 1 from the player (if not creative) and put it on the SCCS pillar
                blockEntity.heldItemStack = itemUsed.copy()
                blockEntity.heldItemStack.count = 1
                if (!player.isCreative) {
                    itemUsed.decrement(1)
                }

                // Play some sounds
                (player as ServerPlayerEntity).networkHandler.sendPacket(PlaySoundIdS2CPacket(
                        SoundEvents.BLOCK_ENDER_CHEST_OPEN.id,
                        SoundCategory.BLOCKS,
                        Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5),
                        0.3F,
                        2.8F)
                )
                player.networkHandler.sendPacket(PlaySoundIdS2CPacket(
                        SoundEvents.BLOCK_END_PORTAL_FRAME_FILL.id,
                        SoundCategory.BLOCKS,
                        Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5),
                        0.5F,
                        3.8F)
                )
            }
        }

        blockEntity.sendUpdateStatus()

        return ActionResult.SUCCESS
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun onBreak(world: World?, pos: BlockPos?, state: BlockState?, player: PlayerEntity?) {
        // Drop held items
        super.onBreak(world, pos, state, player)
        val entity = world!!.getBlockEntity(pos) as SCCSBlockEntity
        if (pos != null) {
            ItemScatterer.spawn(world, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, entity.heldItemStack)
        }
    }
}
