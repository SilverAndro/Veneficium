/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.spell_components.collection

import net.minecraft.util.math.Direction
import net.vene.common.magic.spell_components.MoveComponent
import net.vene.common.magic.event.EventListenerResult
import net.vene.common.magic.handling.HandlerOperation
import net.vene.common.magic.util.LogicHelper

@Suppress("unused")
object MoveComponents {
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

        if (LogicHelper.executeOnce(context, keyRegistered)) {
            context.executor.events.hitGround.register {
                val velocity = context.executor.velocity

                context.executor.pos = context.executor.pos.subtract(velocity.multiply(0.01))

                context.executor.velocity = when (context.dataStorage["hit_ground_direction"] as Direction) {
                    Direction.DOWN, Direction.UP -> context.executor.velocity.multiply(1.0, -1.0, 1.0)
                    Direction.NORTH, Direction.SOUTH -> context.executor.velocity.multiply(1.0, 1.0, -1.0)
                    Direction.WEST, Direction.EAST -> context.executor.velocity.multiply(-1.0, 1.0, 1.0)
                }.multiply(0.95)

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
}