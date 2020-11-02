/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.spell_components.collection

import net.minecraft.util.math.BlockPos
import net.minecraft.world.explosion.Explosion
import net.vene.VeneMain
import net.vene.common.magic.event.EventListenerResult
import net.vene.common.magic.handling.HandlerOperation
import net.vene.common.magic.spell_components.ResultComponent
import net.vene.common.magic.util.LogicHelper
import kotlin.random.Random

@Suppress("unused")
object ResultComponents {
    val TARGET_GROUND_HIT = ResultComponent("target_ground") { context, modifiers, queue ->
        val keyFired = "target_ground_fired"
        val keyRegistered = "target_ground_registered"

        if (LogicHelper.executeOnce(context, keyRegistered)) {
            context.executor.events.hitGround.register {
                if (context.dataStorage["last_air_block"] is BlockPos) {
                    context.targets.add(context.dataStorage["last_air_block"] as BlockPos)
                } else {
                    context.targets.add(BlockPos(context.executor.pos))
                }
                LogicHelper.fire(context, keyFired)
                EventListenerResult.CONTINUE_REMOVE
            }
        }

        if (LogicHelper.didFire(context, keyFired)) {
            LogicHelper.reset(context, listOf(keyFired, keyRegistered))
            HandlerOperation.REMOVE_CONTINUE
        } else {
            HandlerOperation.STAY_STOP
        }
    }

    val EXPLODE = ResultComponent("explode") { context, modifiers, queue ->
        val pos = context.targets.last()
        val explosion = context.world.createExplosion(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 1.6F, false, Explosion.DestructionType.DESTROY)
        if (modifiers.lastOrNull() != null) {
            val material = modifiers.removeLast()
            for (blockPos in explosion.affectedBlocks) {
                if (Random.nextBoolean() && context.world.getBlockState(blockPos).isAir) {
                    context.world.setBlockState(blockPos, material.block.defaultState)
                }
            }
        }
        HandlerOperation.REMOVE_CONTINUE
    }

    val LARGE_EXPLODE = ResultComponent("large_explode") { context, modifiers, queue ->
        val pos = context.targets.last()
        val explosion = context.world.createExplosion(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, 3.2F, false, Explosion.DestructionType.DESTROY)
        if (modifiers.lastOrNull() != null) {
            val material = modifiers.removeLast()
            for (blockPos in explosion.affectedBlocks) {
                if (Random.nextBoolean() && context.world.getBlockState(blockPos).isAir) {
                    context.world.setBlockState(blockPos, material.block.defaultState)
                }
            }
        }
        HandlerOperation.REMOVE_CONTINUE
    }

    val CREATE_FORCE = ResultComponent("create_force") { context, modifiers, queue ->
        val pos = context.targets.last()
        context.world.createExplosion(null, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, -1.5f, false, Explosion.DestructionType.NONE)
        HandlerOperation.REMOVE_CONTINUE
    }

    val CREATE_LIGHT = ResultComponent("create_light") { context, modifiers, queue ->
        val pos = context.targets.last()

        if (context.world.getBlockState(pos).isAir) {
            context.world.setBlockState(pos, VeneMain.LIGHT_BLOCK.defaultState)
        }
        HandlerOperation.REMOVE_CONTINUE
    }
}