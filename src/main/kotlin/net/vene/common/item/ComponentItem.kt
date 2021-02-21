/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.item

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World
import net.vene.magic.spell_components.MagicEffect
import kotlin.math.floor

class ComponentItem(settings: Settings, val effect: MagicEffect) : Item(settings) {
    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        if (effect.instability > 0) {
            tooltip.add(TranslatableText("vene.instability", floor(effect.instability * 100).toInt()).formatted(Formatting.GRAY))
        }
        super.appendTooltip(stack, world, tooltip, context)
    }
}
