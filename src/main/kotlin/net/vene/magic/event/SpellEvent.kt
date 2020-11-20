/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.event

import net.vene.VeneMain
import net.vene.magic.SpellContext

class SpellEvent {

    private val listeners: MutableList<EventListener> = mutableListOf()
    private val toBeRemoved: MutableList<EventListener> = mutableListOf()

    fun fire(context: SpellContext) {
        var tmpIndex = 0
        while (tmpIndex < listeners.size) {
            try {
                val listener = listeners[tmpIndex]
                // Run listener and get the result
                when (listener(context)) {
                    EventListenerResult.CONTINUE -> {
                        tmpIndex++
                    }
                    EventListenerResult.STOP -> break
                    EventListenerResult.CONTINUE_REMOVE -> listeners.remove(listener)
                    EventListenerResult.STOP_REMOVE -> {
                        toBeRemoved.add(listener)
                        break
                    }
                }
            } catch (t: Throwable) {
                VeneMain.LOGGER.error("Exception while running listener!")
                t.stackTrace.iterator().forEach {
                    VeneMain.LOGGER.error(it.toString())
                }
                return
            }
        }

        listeners.removeAll(toBeRemoved)
    }

    fun register(newListener: EventListener) {
        listeners.add(newListener)
    }
}

typealias EventListener = (SpellContext) -> EventListenerResult