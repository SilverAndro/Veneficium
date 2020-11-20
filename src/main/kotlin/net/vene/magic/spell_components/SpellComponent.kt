/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.spell_components

import net.vene.magic.SpellContext
import net.vene.magic.handling.HandlerOperation
import net.vene.magic.handling.SpellQueue
import net.vene.magic.spell_components.types.ComponentType
import net.vene.magic.spell_components.types.MaterialComponent

typealias SpellRunnable = (SpellContext, modifiers: MutableList<MaterialComponent>, SpellQueue) -> HandlerOperation

abstract class MagicEffect(open val name: String, private val spellMethod: SpellRunnable) {
    abstract val type: ComponentType

    open fun exec(context: SpellContext, modifiers: MutableList<MaterialComponent>, queue: SpellQueue) : HandlerOperation {
        return spellMethod(context, modifiers, queue)
    }
}