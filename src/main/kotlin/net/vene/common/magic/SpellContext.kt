/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class SpellContext(val world: ServerWorld, caster: SpellCaster, val executor: SpellExecutor) {
    var targets: MutableList<SpellTarget> = mutableListOf()

    val dataStorage: MutableMap<String, Any> = mutableMapOf()

    init {
        targets.add(SpellTarget(caster.entity.blockPos, caster.entity as LivingEntity))
    }

    data class SpellCaster(val entity: Entity, val castedPosition: Vec3d, val castedDirection: Vec3d)
    data class SpellTarget(val pos: BlockPos, val entity: LivingEntity?)
}