/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.compat

import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeDisplay
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.vene.VeneMain
import net.vene.recipe.SCCSRecipe
import javax.lang.model.type.TypeVariable

class REIPlugin : REIPluginV0 {
    override fun getPluginIdentifier(): Identifier {
        return Identifier(VeneMain.MOD_ID, "rei_plugin")
    }

    override fun registerOthers(recipeHelper: RecipeHelper) {
        recipeHelper.registerWorkingStations(weavingIdentifier, weavingEntry)
    }

    override fun registerRecipeDisplays(recipeHelper: RecipeHelper) {
        recipeHelper.registerRecipes(weavingIdentifier, SCCSRecipe::class.java) {
            WeavingDisplay(it)
        }
    }

    override fun registerPluginCategories(recipeHelper: RecipeHelper) {
        recipeHelper.registerCategory(WeavingCategory())
    }

    companion object {
        val weavingIdentifier = Identifier(VeneMain.MOD_ID, "weaving")
        val weavingEntry: EntryStack = EntryStack.create(VeneMain.SCCS_BLOCK)
    }
}
