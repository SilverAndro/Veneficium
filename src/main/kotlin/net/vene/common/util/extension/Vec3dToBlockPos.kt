package net.vene.common.util.extension

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

fun Vec3d.blockpos(): BlockPos {
    return BlockPos(this)
}