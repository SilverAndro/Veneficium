/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.util

import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.vene.cca_component.WandSpellsComponent

fun appendTooltipFromStack(stack: ItemStack, tooltip: MutableList<Text>, context: TooltipContext) {
    val spells = WandSpellsComponent.getSpellsFrom(stack)

    // If advanced display some more info
    if (context.isAdvanced) {
        tooltip.add(LiteralText("Components: ${spells.filterNotNull().size}/${spells.size}").formatted(Formatting.YELLOW))
    }

    // For each component get its display name and add it
    for (component in stack.let { WandSpellsComponent.getSpellsFrom(it) }) {
        if (component != null) {
            tooltip.add(
                LiteralText("+ ").append(
                    TranslatableText("item.vene.${component.type.toString().toLowerCase()}.${component.name}").formatted(Formatting.GREEN)
                )
            )
        }
    }
}
