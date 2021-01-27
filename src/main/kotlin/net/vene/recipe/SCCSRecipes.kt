/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.recipe

import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.world.World
import net.vene.VeneMain

data class SCCSRecipe(val identifier: Identifier, val core: Item, val ingredients: List<Item>, val result: Item) : Recipe<SimpleInventory> {
    override fun matches(inv: SimpleInventory, world: World): Boolean {
        TODO("Not yet implemented")
    }

    override fun craft(inv: SimpleInventory?): ItemStack {
        TODO("Not yet implemented")
    }

    override fun fits(width: Int, height: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getOutput(): ItemStack {
        return ItemStack(result)
    }

    override fun getId(): Identifier {
        return identifier
    }

    override fun getSerializer(): RecipeSerializer<*> {
        return VeneMain.SCCS_RECIPE_SERIALIZER
    }

    override fun getType(): RecipeType<*> {
        return VeneMain.SCCS_RECIPE
    }

    override fun toString(): String {
        return "SCCSRecipe(core=$core, ingredients=$ingredients, result=$result)"
    }
}

// Actual recipes are under root/init/StaticDataAdder.kt
object SCCSRecipeList {
    private val recipes: MutableList<SCCSRecipe> = mutableListOf()

    fun add(recipe: SCCSRecipe): SCCSRecipeList {
        recipes.add(recipe)
        return this
    }

    fun coreHasRecipe(item: Item): Boolean {
        // Returns if there is any recipe that has that core
        return recipes.any {
            it.core == item
        }
    }

    fun craft(core: Item, ingredients: MutableList<Item>): Item {
        // Get all the possible recipes
        val possibleRecipes = recipes.filter {
            it.core == core
        }

        for (possible in possibleRecipes) {
            // Check if ingredients deep match
            val copy = mutableListOf<Item>()
            copy.addAll(possible.ingredients)
            for (ingredient in ingredients) {
                if (copy.contains(ingredient)) {
                    copy.remove(ingredient)
                }
            }

            // All ingredients matched
            if (copy.isEmpty()) {
                return possible.result
            }
        }
        return Items.AIR
    }
}
