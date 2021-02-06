/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.util

// Formats underscored names for display
fun String.displayFromUnderscored(): String {
    val split = this.split("_")

    return buildString {
        for (i in split.indices) {
            append(split[i].capitalize() + " ")
        }
    }.trim()
}
