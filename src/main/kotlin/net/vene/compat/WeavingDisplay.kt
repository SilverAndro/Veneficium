package net.vene.compat

import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.TransferRecipeDisplay
import me.shedaniel.rei.server.ContainerInfo
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.Identifier
import net.vene.recipe.SCCSRecipe

class WeavingDisplay(recipe: SCCSRecipe) : TransferRecipeDisplay {
    override fun getInputEntries(): MutableList<MutableList<EntryStack>> {
        TODO("Not yet implemented")
    }

    override fun getRecipeCategory(): Identifier {
        return REIPlugin.weavingIdentifier
    }

    override fun getWidth(): Int {
        TODO("Not yet implemented")
    }

    override fun getHeight(): Int {
        TODO("Not yet implemented")
    }

    override fun getOrganisedInputEntries(
        containerInfo: ContainerInfo<ScreenHandler>?,
        container: ScreenHandler?
    ): MutableList<MutableList<EntryStack>> {
        TODO("Not yet implemented")
    }
}
