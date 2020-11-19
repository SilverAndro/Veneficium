/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.spell_components.collection

import net.minecraft.block.Blocks
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.explosion.Explosion
import net.vene.VeneMain
import net.vene.common.magic.SpellContext.SpellTarget
import net.vene.common.magic.event.EventListenerResult
import net.vene.common.magic.handling.HandlerOperation
import net.vene.common.magic.spell_components.ComponentFactory
import net.vene.common.magic.spell_components.types.ResultComponent
import net.vene.common.util.LogicHelper
import net.vene.common.util.extension.blockpos
import kotlin.random.Random

@Suppress("unused")
object ResultComponentCollection {
    /*

    TARGETING
    This group is for components that add targets

    */
    val TARGET_GROUND_HIT = ResultComponent("target_ground") { context, modifiers, queue ->
        val keyFired = "target_ground_fired"
        val keyRegistered = "target_ground_registered"

        if (LogicHelper.executeOnce(context, keyRegistered)) {
            context.executor.events.hitGround.register {
                if (context.dataStorage["last_air_block"] is BlockPos) {
                    context.targets.add(SpellTarget(context.dataStorage["last_air_block"] as BlockPos, null))
                } else {
                    context.targets.add(SpellTarget(context.executor.pos.blockpos(), null))
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

    val TARGET_ENTITY = ResultComponent("target_entity") { context, modifiers, queue ->
        val keyFired = "target_entity_fired"
        val keyRegistered = "entity_registered"

        if (LogicHelper.executeOnce(context, keyRegistered)) {
            context.executor.events.moveTick.register {
                val colliding = it.world.getOtherEntities(null, VoxelShapes.cuboid(
                        it.executor.pos.x - 0.2,
                        it.executor.pos.y - 0.2,
                        it.executor.pos.z - 0.2,
                        it.executor.pos.x + 0.2,
                        it.executor.pos.y + 0.2,
                        it.executor.pos.z + 0.2,
                ).boundingBox)
                return@register if (colliding.isEmpty() || colliding.first() !is LivingEntity) {
                    EventListenerResult.STOP
                } else {
                    it.targets.add(SpellTarget(it.executor.pos.blockpos(), colliding.first() as LivingEntity))
                    LogicHelper.fire(context, keyFired)
                    EventListenerResult.CONTINUE_REMOVE
                }
            }
        }

        if (LogicHelper.didFire(context, keyFired)) {
            LogicHelper.reset(context, listOf(keyFired, keyRegistered))
            HandlerOperation.REMOVE_CONTINUE
        } else {
            HandlerOperation.STAY_STOP
        }
    }

    val TARGET_ENTITY_OR_GROUND = ResultComponent("target_entity_or_ground") { context, modifiers, queue ->
        val keyFired = "target_either"
        val keyRegistered = "either_registered"

        if (LogicHelper.executeOnce(context, keyRegistered)) {
            context.executor.events.hitGround.register {
                if (LogicHelper.didFire(context, keyFired)) {
                    return@register EventListenerResult.CONTINUE_REMOVE
                }

                if (context.dataStorage["last_air_block"] is BlockPos) {
                    context.targets.add(SpellTarget(context.dataStorage["last_air_block"] as BlockPos, null))
                } else {
                    context.targets.add(SpellTarget(context.executor.pos.blockpos(), null))
                }
                LogicHelper.fire(context, keyFired)
                EventListenerResult.CONTINUE_REMOVE
            }

            context.executor.events.moveTick.register {
                if (LogicHelper.didFire(context, keyFired)) {
                    return@register EventListenerResult.CONTINUE_REMOVE
                }

                if (context.executor.age > 3) {
                    val colliding = it.world.getOtherEntities(null, VoxelShapes.cuboid(
                            it.executor.pos.x - 0.2,
                            it.executor.pos.y - 0.2,
                            it.executor.pos.z - 0.2,
                            it.executor.pos.x + 0.2,
                            it.executor.pos.y + 0.2,
                            it.executor.pos.z + 0.2,
                    ).boundingBox)
                    return@register if (colliding.isEmpty() || colliding.first() !is LivingEntity) {
                        EventListenerResult.STOP
                    } else {
                        it.targets.add(SpellTarget(it.executor.pos.blockpos(), colliding.first() as LivingEntity))
                        LogicHelper.fire(context, keyFired)
                        EventListenerResult.CONTINUE_REMOVE
                    }
                } else {
                    EventListenerResult.STOP
                }
            }
        }

        if (LogicHelper.didFire(context, keyFired)) {
            LogicHelper.reset(context, listOf(keyFired, keyRegistered))
            HandlerOperation.REMOVE_CONTINUE
        } else {
            HandlerOperation.STAY_STOP
        }
    }

    val TARGET_CURRENT = ResultComponent("target_current") { context, modifiers, queue ->
        context.targets.add(SpellTarget(context.executor.pos.blockpos(), null))
        HandlerOperation.REMOVE_CONTINUE
    }

    /*

    EFFECTS
    This group is for components that have some effect on the world

    */
    val EXPLODE = ResultComponent("explode") { context, modifiers, queue ->
        val spellTarget = context.targets.last()
        val explosion = context.world.createExplosion(null, spellTarget.pos.x + 0.5, spellTarget.pos.y + 0.5, spellTarget.pos.z + 0.5, 1.6F, false, Explosion.DestructionType.DESTROY)
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
        val spellTarget = context.targets.last()
        val explosion = context.world.createExplosion(null, spellTarget.pos.x + 0.5, spellTarget.pos.y + 0.5, spellTarget.pos.z + 0.5, 3.2F, false, Explosion.DestructionType.DESTROY)
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
        val spellTarget = context.targets.last()
        context.world.createExplosion(null, spellTarget.pos.x + 0.5, spellTarget.pos.y + 0.5, spellTarget.pos.z + 0.5, -1.5f, false, Explosion.DestructionType.NONE)
        HandlerOperation.REMOVE_CONTINUE
    }

    val CREATE_LIGHT = ResultComponent("create_light") { context, modifiers, queue ->
        val spellTarget = context.targets.last()

        if (context.world.getBlockState(spellTarget.pos).isAir) {
            context.world.setBlockState(spellTarget.pos, VeneMain.LIGHT_BLOCK.defaultState)
        }
        HandlerOperation.REMOVE_CONTINUE
    }

    val CREATE_MATERIAL = ResultComponent("create_material") { context, modifiers, queue ->
        val spellTarget = context.targets.last()
        if (modifiers.lastOrNull() != null) {
            val material = modifiers.removeLast()
            if (context.world.getBlockState(spellTarget.pos).isAir) {
                context.world.setBlockState(spellTarget.pos, material.block.defaultState)
            }
        }
        HandlerOperation.REMOVE_CONTINUE
    }

    val FREEZE = ResultComponent("freeze") { context, modifiers, queue ->
        val spellTarget = context.targets.last()
        for (x in -2 until 3) {
            for (z in -2 until 3) {
                for (y in -2 until 3) {
                    val pos = BlockPos(context.executor.pos.add(x.toDouble(), y.toDouble(), z.toDouble()))
                    if (context.world.getBlockState(pos) == Blocks.WATER.defaultState) {
                        context.world.setBlockState(pos, Blocks.FROSTED_ICE.defaultState)
                    }
                }
            }
        }
        if (spellTarget.entity != null) {
            spellTarget.entity.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS, 60, 9, true, false))
            spellTarget.entity.addStatusEffect(StatusEffectInstance(StatusEffects.JUMP_BOOST, 60, 128, true, false))
            spellTarget.entity.velocity = Vec3d.ZERO
        }
        HandlerOperation.REMOVE_CONTINUE
    }

    /*

    META
    This group is for meta components, components that change the execution of other components

    */
    val CAST_2X = ComponentFactory.castXTimesBuilder(2)
    val CAST_3X = ComponentFactory.castXTimesBuilder(3)
    val CAST_5X = ComponentFactory.castXTimesBuilder(5)
    val CAST_7X = ComponentFactory.castXTimesBuilder(7)
    val CAST_10X = ComponentFactory.castXTimesBuilder(10)
}