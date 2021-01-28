/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.compat

import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.TransferRecipeDisplay
import me.shedaniel.rei.server.ContainerInfo
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.Identifier
import net.vene.recipe.SCCSRecipe

class WeavingDisplay(val recipe: SCCSRecipe) : TransferRecipeDisplay {
    @ExperimentalStdlibApi
    val usedItems: MutableList<EntryStack> = buildList {
        addAll(EntryStack.ofItems(listOf(recipe.core)))
        addAll(EntryStack.ofItems(recipe.ingredients as Collection<ItemConvertible>))
    }.toMutableList()

    @ExperimentalStdlibApi
    override fun getInputEntries(): MutableList<MutableList<EntryStack>> {
        return mutableListOf(usedItems)
    }

    override fun getRecipeCategory(): Identifier {
        return REIPlugin.weavingIdentifier
    }

    override fun getWidth(): Int {
        return -1
    }

    override fun getHeight(): Int {
        return -1
    }

    @ExperimentalStdlibApi
    override fun getOrganisedInputEntries(
        containerInfo: ContainerInfo<ScreenHandler>?,
        container: ScreenHandler?
    ): MutableList<MutableList<EntryStack>> {
        return mutableListOf(usedItems)
    }

    override fun getResultingEntries(): MutableList<MutableList<EntryStack>> {
        return mutableListOf(mutableListOf(EntryStack.create(recipe.result)))
    }
}
