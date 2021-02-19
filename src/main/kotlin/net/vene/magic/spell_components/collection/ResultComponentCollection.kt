/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.spell_components.collection

import net.minecraft.block.Blocks
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.explosion.Explosion
import net.vene.ConfigInstance
import net.vene.VeneMain
import net.vene.common.util.LogicHelper.didFire
import net.vene.common.util.LogicHelper.executeOnce
import net.vene.common.util.LogicHelper.fire
import net.vene.common.util.LogicHelper.reset
import net.vene.common.util.extension.blockpos
import net.vene.magic.SpellContext.SpellTarget
import net.vene.magic.event.EventListenerResult
import net.vene.magic.handling.HandlerOperation
import net.vene.magic.spell_components.ComponentFactories
import net.vene.magic.spell_components.types.ResultComponent
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

        executeOnce(context, keyRegistered) {
            context.executor.events.hitGround.register {
                if (context.data.contains("last_air_block")) {
                    context.targets.add(SpellTarget(context.data.get("last_air_block"), null))
                } else {
                    context.targets.add(SpellTarget(context.executor.pos.blockpos(), null))
                }
                fire(context, keyFired)
                EventListenerResult.REMOVE_CONTINUE
            }
        }

        if (didFire(context, keyFired)) {
            reset(context, listOf(keyFired, keyRegistered))
            HandlerOperation.REMOVE_CONTINUE
        } else {
            HandlerOperation.STAY_STOP
        }
    }

    val TARGET_ENTITY = ResultComponent("target_entity") { context, modifiers, queue ->
        val keyFired = "target_entity_fired"
        val keyRegistered = "entity_registered"

        executeOnce(context, keyRegistered) {
            context.executor.events.moveTick.register {
                val colliding = it.world.getOtherEntities(null, VoxelShapes.cuboid(
                        it.executor.pos.x - 0.2,
                        it.executor.pos.y - 0.2,
                        it.executor.pos.z - 0.2,
                        it.executor.pos.x + 0.2,
                        it.executor.pos.y + 0.2,
                        it.executor.pos.z + 0.2,
                ).boundingBox)
                return@register if (colliding.isEmpty() || colliding.first() !is LivingEntity || colliding.first() == context.caster.entity) {
                    EventListenerResult.STAY_STOP
                } else {
                    it.targets.add(SpellTarget(it.executor.pos.blockpos(), colliding.first() as LivingEntity))
                    fire(context, keyFired)
                    EventListenerResult.REMOVE_CONTINUE
                }
            }
        }

        if (didFire(context, keyFired)) {
            reset(context, listOf(keyFired, keyRegistered))
            HandlerOperation.REMOVE_CONTINUE
        } else {
            HandlerOperation.STAY_STOP
        }
    }

    val TARGET_ENTITY_OR_GROUND = ResultComponent("target_entity_or_ground") { context, modifiers, queue ->
        val keyFired = "target_either"
        val keyRegistered = "either_registered"

        executeOnce(context, keyRegistered) {
            context.executor.events.hitGround.register {
                if (didFire(context, keyFired)) {
                    return@register EventListenerResult.REMOVE_CONTINUE
                }

                if (context.data.contains("last_air_block")) {
                    context.targets.add(SpellTarget(context.data.get("last_air_block"), null))
                } else {
                    context.targets.add(SpellTarget(context.executor.pos.blockpos(), null))
                }
                fire(context, keyFired)
                EventListenerResult.REMOVE_CONTINUE
            }

            context.executor.events.moveTick.register {
                if (didFire(context, keyFired)) {
                    return@register EventListenerResult.REMOVE_CONTINUE
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
                    return@register if (colliding.isEmpty() || colliding.first() !is LivingEntity || colliding.first() == context.caster.entity) {
                        EventListenerResult.STAY_STOP
                    } else {
                        it.targets.add(SpellTarget(it.executor.pos.blockpos(), colliding.first() as LivingEntity))
                        fire(context, keyFired)
                        EventListenerResult.REMOVE_CONTINUE
                    }
                } else {
                    EventListenerResult.STAY_STOP
                }
            }
        }

        if (didFire(context, keyFired)) {
            reset(context, listOf(keyFired, keyRegistered))
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
    val DAMAGE_ENTITY = ResultComponent("damage_entity") { context, modifiers, queue ->
        val spellTarget = context.targets.last()
        if (spellTarget.entity != null) {
            spellTarget.entity.damage(DamageSource.magic(context.caster.entity, null), 4f)
        }
        HandlerOperation.REMOVE_CONTINUE
    }

    val EXPLODE = ResultComponent("explode") { context, modifiers, queue ->
        val spellTarget = context.targets.last()
        val explosion = context.world.createExplosion(null, spellTarget.pos.x + 0.5, spellTarget.pos.y + 0.5, spellTarget.pos.z + 0.5, 1.6F, false, Explosion.DestructionType.DESTROY)
        if (modifiers.lastOrNull() != null && ConfigInstance.explosionsCreateMaterials) {
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
        if (modifiers.lastOrNull() != null && ConfigInstance.explosionsCreateMaterials) {
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
            } else if (context.data.contains("last_air_block")) {
                context.world.setBlockState(context.data.get("last_air_block"), material.block.defaultState)
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
    val WAIT_1 = ComponentFactories.waitXTicksBuilder(1)
    val WAIT_3 = ComponentFactories.waitXTicksBuilder(3)
    val WAIT_5 = ComponentFactories.waitXTicksBuilder(5)
    val WAIT_10 = ComponentFactories.waitXTicksBuilder(10)
    val WAIT_20 = ComponentFactories.waitXTicksBuilder(20)

    val CHANCE_50 = ResultComponent("chance_50") { context, modifiers, queue ->
        if (Random.nextBoolean()) {
            try {
                queue.handleOp(HandlerOperation.REMOVE_CONTINUE, queue.componentList[queue.tmpIndex + 1])
            } catch (ignored: IndexOutOfBoundsException) {}
        }
        HandlerOperation.REMOVE_CONTINUE
    }

    val CHANCE_25 = ResultComponent("chance_25") { context, modifiers, queue ->
        if (Random.nextBoolean() && Random.nextBoolean()) {
            try {
                queue.handleOp(HandlerOperation.REMOVE_CONTINUE, queue.componentList[queue.tmpIndex + 1])
            } catch (ignored: IndexOutOfBoundsException) {}
        }
        HandlerOperation.REMOVE_CONTINUE
    }

    val AWAIT_DEATH = ResultComponent("await_death") { context, modifiers, queue ->
        val keyFired = "await_death_fired"
        val keyRegistered = "await_death_registered"

        executeOnce(context, keyRegistered) {
            context.executor.events.spellDeath.register {
                fire(context, keyFired)
                EventListenerResult.REMOVE_CONTINUE
            }
        }

        if (didFire(context, keyFired)) {
            reset(context, listOf(keyFired, keyRegistered))
            HandlerOperation.REMOVE_CONTINUE
        } else {
            HandlerOperation.STAY_STOP
        }
    }
}
