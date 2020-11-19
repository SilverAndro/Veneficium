package net.vene.common.magic.spell_components

import net.vene.common.magic.event.EventListenerResult
import net.vene.common.magic.handling.HandlerOperation
import net.vene.common.magic.spell_components.types.ResultComponent
import net.vene.common.util.LogicHelper

object ComponentFactory {
    // Builds "Cast X Times" Components because their all basically the same
    fun castXTimesBuilder(times: Int): ResultComponent {
        return ResultComponent("cast_${times}_times") { context, modifiers, queue ->
            val shouldCallOthersCounterKey = "cast_${times}_do_cast_counter"
            val shouldCallOthersKey = "cast_${times}_do_cast"
            val castRepeatedlyRegistered = "cast_${times}_registered"
            val shouldUnregister = "cast_${times}_unregister"

            if (LogicHelper.executeOnce(context, castRepeatedlyRegistered)) {
                context.executor.events.physicsTick.register {
                    if (LogicHelper.executeXTimes(context, shouldCallOthersCounterKey, times)) {
                        LogicHelper.fire(context, shouldCallOthersKey)
                    } else {
                        LogicHelper.reset(context, listOf(shouldCallOthersCounterKey, castRepeatedlyRegistered, shouldCallOthersKey))
                        LogicHelper.fire(context, shouldUnregister)
                        return@register EventListenerResult.CONTINUE_REMOVE
                    }
                    return@register EventListenerResult.CONTINUE
                }
            }

            if (LogicHelper.didFire(context, shouldUnregister)) {
                queue.ignoreRemoveRequest = false
                return@ResultComponent HandlerOperation.REMOVE_CONTINUE
            }

            queue.ignoreRemoveRequest = true
            return@ResultComponent if (LogicHelper.didFire(context, shouldCallOthersKey)) {
                LogicHelper.reset(context, shouldUnregister)
                HandlerOperation.STAY_CONTINUE
            } else {
                HandlerOperation.STAY_STOP
            }
        }
    }
}