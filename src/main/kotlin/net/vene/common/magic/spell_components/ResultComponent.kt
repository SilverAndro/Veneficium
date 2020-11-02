/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.spell_components

import net.vene.VeneMain

class ResultComponent(override val name: String, spellMethod: SpellRunnable) : MagicEffect(name, spellMethod) {
    override val type: ComponentType = ComponentType.RESULT

    override fun toString(): String {
        return "ResultEffect[$name]"
    }

    init {
        VeneMain.RESULT_COMPONENTS.add(this)
    }
}