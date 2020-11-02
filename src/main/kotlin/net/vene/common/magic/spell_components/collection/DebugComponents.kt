/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.spell_components.collection

import net.vene.common.magic.spell_components.ResultComponent
import net.vene.common.magic.handling.HandlerOperation

@Suppress("unused")
object DebugComponents {
    val THROW_THROWABLE = ResultComponent("error_throw_throwable") { context, modifiers, queue ->
        throw Throwable("DEBUG THROWABLE")
    }

    val NO_OP = ResultComponent("no_op") { context, modifiers, queue -> HandlerOperation.REMOVE_CONTINUE }
}