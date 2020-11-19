/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.spell_components.collection

import net.minecraft.block.Blocks
import net.vene.common.magic.spell_components.MaterialComponent

@Suppress("unused")
object MaterialComponentCollection {
    val DIRT = MaterialComponent("dirt", Blocks.DIRT)
    val WATER = MaterialComponent("water", Blocks.WATER)
    val GRAVEL = MaterialComponent("gravel", Blocks.GRAVEL)
    val COBBLESTONE = MaterialComponent("cobblestone", Blocks.COBBLESTONE)
}