/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.spell_components

import net.vene.magic.event.EventListenerResult
import net.vene.magic.handling.HandlerOperation
import net.vene.magic.handling.SpellQueue
import net.vene.magic.spell_components.types.ResultComponent
import net.vene.common.util.LogicHelper.didFire
import net.vene.common.util.LogicHelper.executeOnce
import net.vene.common.util.LogicHelper.executeXTimes
import net.vene.common.util.LogicHelper.fire
import net.vene.common.util.LogicHelper.reset

object ComponentFactories {
    // Builds "Cast X Times" Components because their all basically the same
    fun castXTimesBuilder(times: Int): ResultComponent {
        return ResultComponent("cast_${times}_times") { context, modifiers, queue ->
            val loadedCounter = "cast_${times}_do_cast_counter"
            val doSaveState = "cast_${times}_has_saved"

            val componentsSavedKey = "cast_${times}_components"
            val datastorageKey = "cast_${times}_data"

            executeOnce(context, doSaveState) {
                // Save the states
                context.dataStorage[componentsSavedKey] = queue.copy()
                // haha yes epic serialization
                // The reason for this is every time we read we unpack this once, so we nest it X times deep so on the Xth time reading it cleans itself up
                //     and doesn't cause any ClassCastExceptions
                for (i in 0 until times) {
                    context.dataStorage[datastorageKey] = context.dataStorage.toMap()
                }
            }

            if (!executeXTimes(context, loadedCounter, times)) {
                reset(context, listOf(loadedCounter, loadedCounter))
                return@ResultComponent HandlerOperation.REMOVE_CONTINUE
            }

            // Load the states
            queue.acquireStateOfCopy(context.dataStorage[componentsSavedKey] as SpellQueue)

            val executedCount = context.dataStorage[loadedCounter]
            @Suppress("UNCHECKED_CAST")
            context.dataStorage = context.dataStorage[datastorageKey] as MutableMap<String, Any>
            // Reset this so we don't reset the amount of times we ran
            context.dataStorage[loadedCounter] = executedCount as Any
            return@ResultComponent HandlerOperation.STAY_CONTINUE
        }
    }

    fun waitXTicksBuilder(ticks: Int): ResultComponent {
        return ResultComponent("wait_${ticks / 20.0}_seconds") { context, modifiers, queue ->
            val counterKey = "wait_$ticks"
            val shouldUnregister = "wait_${ticks}_unregister"
            val isRegistered = "wait_${ticks}_registered"

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

            HandlerOperation.FREEZE
        }
    }
}