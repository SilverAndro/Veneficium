/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.util

import net.vene.VeneMain
import net.vene.common.magic.SpellContext

@Suppress("MemberVisibilityCanBePrivate")
object LogicHelper {
    fun executeOnce(context: SpellContext, key: String): Boolean {
        return if (context.dataStorage[key] != true) {
            context.dataStorage[key] = true
            true
        } else {
            false
        }
    }

    fun executeXTimes(context: SpellContext, key: String, count: Int): Boolean {
        if (!context.dataStorage.containsKey(key)) {
            context.dataStorage[key] = 0
        }
        return if ((context.dataStorage[key] as Int) < count) {
            context.dataStorage[key] = context.dataStorage[key] as Int + 1
            true
        } else {
            false
        }
    }

    fun fire(context: SpellContext, key: String) {
        context.dataStorage[key] = true
    }

    fun didFire(context: SpellContext, key: String): Boolean {
        return context.dataStorage[key] == true
    }

    fun reset(context: SpellContext, key: String) {
        when (context.dataStorage[key]) {
            is Boolean -> context.dataStorage[key] = false
            is Int -> context.dataStorage[key] = 0
            else -> VeneMain.LOGGER.warn("Unable to reset object ${context.dataStorage[key]} with key $key")
        }
    }

    fun reset(context: SpellContext, keys: List<String>) {
        for (key in keys) {
            reset(context, key)
        }
    }
}