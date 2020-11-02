/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.util

object StringUtil {
    // Formats underscored names for display
    fun displayFromUnderscored(input: String): String {
        val split = input.split("_")

        val builder = StringBuilder()
        for (i in split.indices) {
            builder.append(split[i].capitalize() + " ")
        }
        return builder.trim().toString()
    }
}