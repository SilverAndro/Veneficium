/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.spell_components.collection

import net.minecraft.util.math.Direction
import net.vene.magic.spell_components.types.MoveComponent
import net.vene.magic.event.EventListenerResult
import net.vene.magic.handling.HandlerOperation
import net.vene.common.util.LogicHelper

@Suppress("unused")
object MoveComponentCollection {
    val NO_GRAV = MoveComponent("no_gravity") { context, modifiers, queue ->
        context.executor.gravity = 0.0
        HandlerOperation.REMOVE_CONTINUE
    }

    val HIGH_GRAV = MoveComponent("high_gravity") { context, modifiers, queue ->
        context.executor.gravity *= 2
        HandlerOperation.REMOVE_CONTINUE
    }

    val LOW_GRAV = MoveComponent("low_gravity") { context, modifiers, queue ->
        context.executor.gravity /= 2
        HandlerOperation.REMOVE_CONTINUE
    }

    val REVERSE_GRAV = MoveComponent("reverse_gravity") { context, modifiers, queue ->
        context.executor.gravity *= -1
        HandlerOperation.REMOVE_CONTINUE
    }

    val BOUNCE = MoveComponent("bounce") { context, modifiers, queue ->
        val keyFired = "bounce_fired"
        val keyRegistered = "bounce_registered"

        LogicHelper.executeOnce(context, keyRegistered) {
            context.executor.events.hitGround.register {
                val velocity = context.executor.velocity

                context.executor.pos = context.executor.pos.subtract(velocity.multiply(0.01))

                context.executor.velocity = when (context.dataStorage["hit_ground_direction"] as Direction) {
                    Direction.DOWN, Direction.UP -> context.executor.velocity.multiply(1.0, -1.0, 1.0)
                    Direction.NORTH, Direction.SOUTH -> context.executor.velocity.multiply(1.0, 1.0, -1.0)
                    Direction.WEST, Direction.EAST -> context.executor.velocity.multiply(-1.0, 1.0, 1.0)
                }.multiply(0.9)

                LogicHelper.fire(context, keyFired)
                EventListenerResult.REMOVE_CONTINUE
            }
        }

        if (LogicHelper.didFire(context, keyFired)) {
            LogicHelper.reset(context, listOf(keyFired, keyRegistered))
            HandlerOperation.REMOVE_CONTINUE
        } else {
            HandlerOperation.STAY_STOP
        }
    }

    val SLOW_DOWN = MoveComponent("slow_down") { context, modifiers, queue ->
        context.executor.velocity = context.executor.velocity.multiply(0.5)
        context.executor.gravity *= 0.5
        HandlerOperation.REMOVE_CONTINUE
    }

    val SPEED_UP = MoveComponent("speed_up") { context, modifiers, queue ->
        context.executor.velocity = context.executor.velocity.multiply(2.0)
        context.executor.gravity *= 2
        HandlerOperation.REMOVE_CONTINUE
    }

    val ACCELERATE = MoveComponent("accelerate") { context, modifiers, queue ->
        val executeCount = "accelerate_execute_count"
        val shouldUnregister = "accelerate_unregister"

        context.executor.events.gameTick.register {
            if (LogicHelper.executeXTimes(context, executeCount, 20)) {
                context.executor.velocity = context.executor.velocity.multiply(1.17)
                context.executor.gravity *= 1.17
                return@register EventListenerResult.STAY_CONTINUE
            }
            LogicHelper.fire(context, shouldUnregister)
            EventListenerResult.REMOVE_CONTINUE
        }

        if (LogicHelper.didFire(context, shouldUnregister)) {
            LogicHelper.reset(context, listOf(executeCount, shouldUnregister))
            return@MoveComponent HandlerOperation.REMOVE_CONTINUE
        }

        HandlerOperation.STAY_CONTINUE
    }
}
