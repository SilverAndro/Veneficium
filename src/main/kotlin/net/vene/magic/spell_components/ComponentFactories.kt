/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.spell_components

import net.minecraft.particle.ParticleEffect
import net.vene.common.util.LogicHelper.didFire
import net.vene.common.util.LogicHelper.executeOnce
import net.vene.common.util.LogicHelper.executeXTimes
import net.vene.common.util.LogicHelper.fire
import net.vene.common.util.LogicHelper.reset
import net.vene.magic.event.EventListenerResult
import net.vene.magic.handling.HandlerOperation
import net.vene.magic.spell_components.types.CosmeticComponent
import net.vene.magic.spell_components.types.ResultComponent

object ComponentFactories {
    fun trailParticleBuilder(particleName: String, particle: ParticleEffect?): CosmeticComponent {
        return CosmeticComponent("${particleName}_trail") { context, modifiers, queue ->
            context.executor.trailEffect = particle
            HandlerOperation.REMOVE_CONTINUE
        }
    }

    fun waitXTicksBuilder(ticks: Int): ResultComponent {
        return ResultComponent("wait_${ticks / 20.0}_seconds") { context, modifiers, queue ->
            val counterKey = "wait_$ticks"
            val shouldUnregister = "wait_${ticks}_unregister"
            val isRegistered = "wait_${ticks}_registered"

            executeOnce(context, isRegistered) {
                context.executor.events.gameTick.register {
                    if (executeXTimes(context, counterKey, ticks)) {
                        return@register EventListenerResult.STAY_STOP
                    } else {
                        fire(context, shouldUnregister)
                        return@register EventListenerResult.REMOVE_CONTINUE
                    }
                }
            }

            if (didFire(context, shouldUnregister)) {
                reset(context, listOf(shouldUnregister, counterKey, isRegistered))
                return@ResultComponent HandlerOperation.REMOVE_CONTINUE
            }

            HandlerOperation.FREEZE
        }
    }
}
