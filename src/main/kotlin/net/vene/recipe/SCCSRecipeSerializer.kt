/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.recipe

import com.google.gson.JsonObject
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class SCCSRecipeSerializer : RecipeSerializer<SCCSRecipe> {
    @ExperimentalStdlibApi
    override fun read(id: Identifier, json: JsonObject): SCCSRecipe {
        val core = Registry.ITEM.get(Identifier(json.get("core").asString))
        val result = Registry.ITEM.get(Identifier(json.get("result").asString))

        val ingredients = buildList {
            json.get("ingredients").asJsonArray.forEach {
                add(Registry.ITEM.get(Identifier(it.asString)))
            }
        }

        if (ingredients.size > 4) {
            throw IndexOutOfBoundsException("Ingredients cannot be larger than 4, found ${json.get("ingredients")}")
        }

        val sccsRecipe = SCCSRecipe(id, core, ingredients, result)

        SCCSRecipeList.add(sccsRecipe)
        return sccsRecipe
    }

    override fun read(id: Identifier, buf: PacketByteBuf): SCCSRecipe {
        println(id)
        println(buf)
        TODO("Not yet implemented")
    }

    override fun write(buf: PacketByteBuf, recipe: SCCSRecipe) {
        println(buf)
        println(recipe)
        TODO("Not yet implemented")
    }
}
