package net.vene.compat

import me.shedaniel.rei.api.BuiltinPlugin
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0
import net.minecraft.util.Identifier
import net.vene.VeneMain


class REIPlugin : REIPluginV0 {
    override fun getPluginIdentifier(): Identifier {
        return Identifier(VeneMain.MOD_ID, "rei_plugin");
    }

    override fun registerOthers(recipeHelper: RecipeHelper) {
        recipeHelper.registerWorkingStations(BuiltinPlugin.CRAFTING, EntryStack.create(VeneMain.SCCS_BLOCK))
    }
}