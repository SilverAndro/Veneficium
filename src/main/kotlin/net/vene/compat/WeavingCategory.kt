/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.compat

import it.unimi.dsi.fastutil.ints.IntList
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.TransferRecipeCategory
import me.shedaniel.rei.api.widgets.Widgets
import me.shedaniel.rei.gui.widget.Widget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.vene.VeneMain


class WeavingCategory : TransferRecipeCategory<WeavingDisplay> {
    override fun getIdentifier(): Identifier {
        return REIPlugin.weavingIdentifier
    }

    override fun getCategoryName(): String {
        return TranslatableText("vene.screen.compat.category.weaving").parse(null, null, 1).string
    }

    override fun renderRedSlots(matrices: MatrixStack?, widgets: MutableList<Widget>?, bounds: Rectangle?, display: WeavingDisplay?, redSlots: IntList?) {}

    override fun getLogo(): EntryStack {
        return REIPlugin.weavingEntry
    }

    override fun getDisplayHeight(): Int {
        return 104
    }

    @ExperimentalStdlibApi
    override fun setupDisplay(recipeDisplay: WeavingDisplay, bounds: Rectangle): MutableList<Widget> {
        val items = recipeDisplay.usedItems
        val widgets: MutableList<Widget> = mutableListOf()
        widgets.addAll(super.setupDisplay(recipeDisplay, bounds))

        val centerX = bounds.centerX - 34
        val centerY = bounds.centerY - 2
        val offset = 28

        widgets.add(Widgets.createTexturedWidget(Identifier(VeneMain.MOD_ID, "textures/gui/weaving.png"), bounds.x, bounds.y, 0F, 0F, 150, 104, 150, 104))

        try {
            widgets.add(
                Widgets.createSlot(Point(centerX, centerY)).entries(listOf(items[0]))
                    .disableBackground().markOutput()
            )
            widgets.add(
                Widgets.createSlot(Point(centerX - offset, centerY - offset)).entries(listOf(items[1]))
                    .disableBackground().markOutput()
            )
            widgets.add(
                Widgets.createSlot(Point(centerX - offset, centerY + offset)).entries(listOf(items[2]))
                    .disableBackground().markOutput()
            )
            widgets.add(
                Widgets.createSlot(Point(centerX + offset, centerY - offset)).entries(listOf(items[3]))
                    .disableBackground().markOutput()
            )
            widgets.add(
                Widgets.createSlot(Point(centerX + offset, centerY + offset)).entries(listOf(items[4]))
                    .disableBackground().markOutput()
            )
        } catch (ignored: IndexOutOfBoundsException) {
            // Do nothing
        }
        widgets.add(
            Widgets.createSlot(Point(centerX + 80, centerY)).entries(listOf(EntryStack.create(recipeDisplay.recipe.result))).disableBackground().markOutput()
        )
        return widgets
    }
}
