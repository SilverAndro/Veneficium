/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.recipe

import com.google.gson.JsonObject
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.vene.common.util.extension.id
import net.vene.common.util.extension.toItem

class SCCSRecipeSerializer : RecipeSerializer<SCCSRecipe> {
    @ExperimentalStdlibApi
    override fun read(id: Identifier, json: JsonObject): SCCSRecipe {
        val core = Registry.ITEM.get(Identifier(json.get("core").asString))
        val result = Registry.ITEM.get(Identifier(json.get("result").asString))

        val ingredients = buildList {
            json.get("ingredients").asJsonArray.forEach {
                add(Identifier(it.asString).toItem())
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
        val result = buf.readIdentifier().toItem()
        val core = buf.readIdentifier().toItem()
        val ingredients: MutableList<Item> = mutableListOf()
        repeat(buf.readByte().toInt()) {
            ingredients.add(buf.readIdentifier().toItem())
        }

        return SCCSRecipe(id, core, ingredients, result)
    }

    override fun write(buf: PacketByteBuf, recipe: SCCSRecipe) {
        buf.writeIdentifier(recipe.result.id())
        buf.writeIdentifier(recipe.core.id())
        buf.writeByte(recipe.ingredients.size)
        recipe.ingredients.forEach {
            buf.writeIdentifier(it.id())
        }
    }
}
