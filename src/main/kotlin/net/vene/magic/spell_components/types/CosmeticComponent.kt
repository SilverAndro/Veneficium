/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.spell_components.types

import net.vene.VeneMain
import net.vene.magic.spell_components.MagicEffect
import net.vene.magic.spell_components.SpellRunnable

class CosmeticComponent(override val name: String, spellMethod: SpellRunnable) : MagicEffect(name, spellMethod) {
    override val type: BuiltinComponentType = BuiltinComponentType.COSMETIC

    override fun toString(): String {
        return "CosmeticEffect[$name]"
    }

    init {
        VeneMain.COSMETIC_COMPONENTS.add(this)
        VeneMain.ALL_COMPONENTS.add(this)
    }
}
