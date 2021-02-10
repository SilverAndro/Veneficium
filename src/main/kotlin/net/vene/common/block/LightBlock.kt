/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.block

import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class LightBlock(settings: Settings?) : Block(settings) {
    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random?) {
        world.addParticle(ParticleTypes.FLAME,
            pos.x + randomDisplayOffset(world),
            pos.y + randomDisplayOffset(world),
            pos.z + randomDisplayOffset(world),
            0.0, 0.0, 0.0)
        world.addParticle(ParticleTypes.FLAME,
            pos.x + randomDisplayOffset(world),
            pos.y + randomDisplayOffset(world),
            pos.z + randomDisplayOffset(world),
            0.0, 0.0, 0.0)
        world.addParticle(ParticleTypes.FLAME,
            true,
            pos.x + randomDisplayOffset(world),
            pos.y + randomDisplayOffset(world),
            pos.z + randomDisplayOffset(world),
            0.0, 0.0, 0.0)
        world.addParticle(ParticleTypes.SMOKE, pos.x + randomDisplayOffset(world), pos.y + 0.5, pos.z + 0.5, 0.0, 0.0, 0.0)
    }

    override fun getOpacity(state: BlockState?, world: BlockView?, pos: BlockPos?): Int {
        return 0
    }

    override fun getOutlineShape(state: BlockState?, world: BlockView?, pos: BlockPos?, context: ShapeContext?): VoxelShape {
        return VoxelShapes.cuboid(Box(0.35, 0.35, 0.35, 0.65, 0.65, 0.65))
    }

    override fun isTranslucent(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
        return true
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.INVISIBLE
    }

    override fun getAmbientOcclusionLightLevel(state: BlockState?, world: BlockView?, pos: BlockPos?): Float {
        return 1.0f
    }

    private fun randomDisplayOffset(world: World): Double {
        return world.random.nextDouble() / 4.0 + 0.4
    }
}
