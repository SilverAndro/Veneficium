/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.util.math

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import kotlin.math.PI

object MathUtil {
    // Taken from bow code I think? Not a fan of the magic numbers but it works
    fun facingToVector(yaw: Float, pitch: Float): Vec3d {
        val f = MathHelper.cos(-yaw * 0.017453292f - PI.toFloat())

        val f1 = MathHelper.sin(-yaw * 0.017453292f - PI.toFloat())

        val f2 = -MathHelper.cos(-pitch * 0.017453292f)

        val f3 = MathHelper.sin(-pitch * 0.017453292f)

        return Vec3d((f1 * f2).toDouble(), f3.toDouble(), (f * f2).toDouble())
    }

    fun blockPosChangeToDirection(first: BlockPos, next: BlockPos): Direction {
        if (first.y > next.y) {
            return Direction.DOWN
        }
        if (first.y < next.y) {
            return Direction.UP
        }

        if (first.x > next.x) {
            return Direction.WEST
        }
        if (first.x < next.x) {
            return Direction.EAST
        }

        if (first.z > next.z) {
            return Direction.NORTH
        }
        if (first.z < next.z) {
            return Direction.SOUTH
        }

        return Direction.DOWN
    }
}