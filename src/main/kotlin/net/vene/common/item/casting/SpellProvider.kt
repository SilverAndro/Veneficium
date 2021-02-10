/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.item.casting

import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

interface SpellProvider {
    fun getMaxSpells(): Int

    fun fireSpells(pos: Vec3d, facing: Vec3d, world: ServerWorld, stack: ItemStack, user: ServerPlayerEntity?)
}
