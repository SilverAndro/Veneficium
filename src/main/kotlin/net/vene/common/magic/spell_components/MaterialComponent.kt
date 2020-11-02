/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.spell_components

import net.minecraft.block.Block
import net.vene.VeneMain
import net.vene.common.magic.handling.HandlerOperation

class MaterialComponent(override val name: String, val block: Block) : MagicEffect(name, { _, _, _ -> HandlerOperation.MATERIAL_MOVE }) {
    override val type = ComponentType.MATERIAL

    override fun toString(): String {
        return "MaterialEffect[$name, $block]"
    }

    init {
        VeneMain.MATERIAL_COMPONENTS.add(this)
    }
}