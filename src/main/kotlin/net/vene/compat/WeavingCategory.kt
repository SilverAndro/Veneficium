package net.vene.compat

import it.unimi.dsi.fastutil.ints.IntList
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.TransferRecipeCategory
import me.shedaniel.rei.gui.widget.Widget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class WeavingCategory : TransferRecipeCategory<WeavingDisplay> {
    override fun getIdentifier(): Identifier {
        return REIPlugin.weavingIdentifier
    }

    override fun getCategoryName(): String {
        return "Weaving"
    }

    override fun renderRedSlots(matrices: MatrixStack?, widgets: MutableList<Widget>?, bounds: Rectangle?, display: WeavingDisplay?, redSlots: IntList?) {}

    override fun getLogo(): EntryStack {
        return REIPlugin.weavingEntry
    }

    override fun setupDisplay(recipeDisplay: WeavingDisplay, bounds: Rectangle): MutableList<Widget> {
        return super.setupDisplay(recipeDisplay, bounds)
    }
}
