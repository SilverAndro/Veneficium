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
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.vene.common.block.entity.WandEditBlockEntity


class WandEditBlock(settings: Settings) : BlockWithEntity(settings) {
    override fun createBlockEntity(blockView: BlockView): BlockEntity? {
        return WandEditBlockEntity()
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos?, player: PlayerEntity, hand: Hand?, hit: BlockHitResult?): ActionResult? {
        // Open the wand edit screen if we can
        if (!world.isClient) {
            val screenHandlerFactory = state.createScreenHandlerFactory(world, pos)
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory)
            }
        }
        return ActionResult.SUCCESS
    }

    override fun getRenderType(state: BlockState?): BlockRenderType? {
        return BlockRenderType.MODEL
    }
}