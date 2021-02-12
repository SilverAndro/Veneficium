/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.handling

class ContextDataHolder {
    private val data: MutableList<DataContainer<Any>> = mutableListOf()

    fun contains(key: String): Boolean {
        return data.any { it.key == key }
    }

    fun set(new: DataContainer<Any>) {
        if (contains(new.key)) {
            data[data.indexOfFirst { it.key == new.key }] = new
        } else {
            data.add(new)
        }
    }

    fun <T> get(key: String): T {
        @Suppress("UNCHECKED_CAST")
        return (data.first { it.key == key } as DataContainer<T>).get()
    }

    fun remove(key: String) {
        data.removeAt(data.indexOfFirst { it.key == key })
    }
}
