/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.vene.magic.handling.ContextDataHolder

class SpellContext(val world: ServerWorld, val caster: SpellCaster, var executor: SpellExecutor) {
    var targets: MutableList<SpellTarget> = mutableListOf()

    val data = ContextDataHolder()

    init {
        targets.add(SpellTarget(caster.entity?.blockPos ?: BlockPos.ORIGIN, caster.entity as LivingEntity?))
    }

    data class SpellCaster(val entity: PlayerEntity?, val castedPosition: Vec3d, val castedDirection: Vec3d)
    data class SpellTarget(val pos: BlockPos, val entity: LivingEntity?)
}
