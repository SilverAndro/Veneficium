/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.util

import net.vene.magic.SpellContext
import net.vene.magic.handling.DataContainer

@Suppress("MemberVisibilityCanBePrivate")
object LogicHelper {
    /**
     * Returns true if the key has not been set before
     */
    fun executeOnce(context: SpellContext, key: String): Boolean {
        return if (!context.data.contains(key)) {
            context.data.set(DataContainer(key, true))
            true
        } else {
            false
        }
    }

    /**
     * Returns true if the key has been set less than X times before
     */
    fun executeXTimes(context: SpellContext, key: String, count: Int): Boolean {
        if (!context.data.contains(key)) {
            context.data.set(DataContainer(key, 0))
        }

        return if (context.data.get<Int>(key) < count) {
            context.data.set(DataContainer(key, context.data.get<Int>(key) + 1))
            true
        } else {
            false
        }
    }

    /**
     * Sets a flag to true
     */
    fun fire(context: SpellContext, key: String) {
        context.data.set(DataContainer(key, true))
    }

    /**
     * Checks if a flag is true
     */
    fun didFire(context: SpellContext, key: String): Boolean {
        return context.data.contains(key) && context.data.get(key)
    }

    /**
     * Resets the states of the key for further components
     */
    fun reset(context: SpellContext, key: String) {
        context.data.remove(key)
    }

    /**
     * Resets all keys
     */
    fun reset(context: SpellContext, keys: List<String>) {
        for (key in keys) {
            reset(context, key)
        }
    }

    /**
     * Executes the lambda if the key has not fired before
     */
    fun executeOnce(context: SpellContext, key: String, method: ()->Unit) {
        if (executeOnce(context, key)) {
            method()
        }
    }
}
