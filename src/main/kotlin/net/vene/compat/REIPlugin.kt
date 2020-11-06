/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

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