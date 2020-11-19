package net.vene.common.magic.spell_components

import net.vene.common.magic.event.EventListenerResult
import net.vene.common.magic.handling.HandlerOperation
import net.vene.common.magic.spell_components.types.ResultComponent
import net.vene.common.util.LogicHelper
import net.vene.common.util.LogicHelper.didFire
import net.vene.common.util.LogicHelper.executeOnce
import net.vene.common.util.LogicHelper.executeXTimes
import net.vene.common.util.LogicHelper.fire
import net.vene.common.util.LogicHelper.reset

object ComponentFactory {
    // Builds "Cast X Times" Components because their all basically the same
    fun castXTimesBuilder(times: Int): ResultComponent {
        return ResultComponent("cast_${times}_times") { context, modifiers, queue ->
            val shouldCallOthersCounterKey = "cast_${times}_do_cast_counter"
            val shouldCallOthersKey = "cast_${times}_do_cast"
            val castRepeatedlyRegistered = "cast_${times}_registered"
            val shouldUnregister = "cast_${times}_unregister"

            executeOnce(context, castRepeatedlyRegistered) {
                context.executor.events.physicsTick.register {
                    if (executeXTimes(context, shouldCallOthersCounterKey, times)) {
                        fire(context, shouldCallOthersKey)
                    } else {
                        reset(context, listOf(shouldCallOthersCounterKey, castRepeatedlyRegistered, shouldCallOthersKey))
                        fire(context, shouldUnregister)
                        return@register EventListenerResult.CONTINUE_REMOVE
                    }
                    return@register EventListenerResult.CONTINUE
                }
            }

            if (didFire(context, shouldUnregister)) {
                queue.ignoreRemoveRequest = false
                reset(context, listOf(shouldUnregister, shouldCallOthersCounterKey))
                return@ResultComponent HandlerOperation.REMOVE_CONTINUE
            }

            queue.ignoreRemoveRequest = true
            return@ResultComponent if (didFire(context, shouldCallOthersKey)) {
                HandlerOperation.STAY_CONTINUE
            } else {
                HandlerOperation.STAY_STOP
            }
        }
    }

    fun waitXTicksBuilder(ticks: Int): ResultComponent {
        return ResultComponent("wait_${ticks / 20.0}_seconds") { context, modifiers, queue ->
            val counterKey = "wait_$ticks"
            val shouldUnregister = "cast_${ticks}_unregister"
            val isRegistered = "cast_${ticks}_registered"

            executeOnce(context, isRegistered) {
                context.executor.events.physicsTick.register {
                    if (executeXTimes(context, counterKey, ticks)) {
                        return@register EventListenerResult.STOP
                    } else {
                        fire(context, shouldUnregister)
                        return@register EventListenerResult.CONTINUE_REMOVE
                    }
                }
            }

            if (didFire(context, shouldUnregister)) {
                reset(context, shouldUnregister)
                return@ResultComponent HandlerOperation.REMOVE_CONTINUE
            }

            HandlerOperation.STAY_STOP
        }
    }
}