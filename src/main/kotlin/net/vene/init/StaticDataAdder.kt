/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.init

import net.devtech.arrp.api.RuntimeResourcePack
import net.devtech.arrp.json.lang.JLang
import net.devtech.arrp.json.loot.JLootTable.*
import net.devtech.arrp.json.recipe.JIngredient.ingredient
import net.devtech.arrp.json.recipe.JKeys.keys
import net.devtech.arrp.json.recipe.JPattern.pattern
import net.devtech.arrp.json.recipe.JRecipe.shaped
import net.devtech.arrp.json.recipe.JResult.item
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.vene.VeneMain
import net.vene.VeneMain.Companion.MOD_ID
import net.vene.recipe.SCCSRecipe
import net.vene.recipe.SCCSRecipeList

// "Static" functions for init-ing data
// Most usages are in VeneMain
object StaticDataAdder {
    fun lang(lang: JLang) {
        lang.translate(
                "item.vene.wand",
                "Magic Wand"
        ).translate(
                "itemGroup.vene.items",
                "Veneficium Items"
        ).translate(
                "itemGroup.vene.components",
                "Veneficium Spell Components"
        ).translate(
                "vene.screen.wand_edit.title",
                "Edit Wands"
        ).translate(
                "item.vene.empty_component",
                "Blank Spell Component"
        ).translate(
                "block.vene.sccs",
                "Weave Focus"
        ).translate(
                "block.vene.wand_edit",
                "Wand Edit Block"
        )
    }

    fun componentRecipes(recipes: SCCSRecipeList) {
        recipes.add(SCCSRecipe(
                // Water
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.WATER_BUCKET, Items.WATER_BUCKET, Items.WATER_BUCKET, Items.WATER_BUCKET),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "water")]!!
        )).add(SCCSRecipe(
                // Fire
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.FLINT_AND_STEEL, Items.LAVA_BUCKET, Items.FIRE_CHARGE, Items.BLAZE_ROD),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "fire")]!!
        )).add(SCCSRecipe(
                // Dirt
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.DIRT, Items.DIRT, Items.COARSE_DIRT, Items.PODZOL),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "dirt")]!!
        )).add(SCCSRecipe(
                // Gravel
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.GRAVEL, Items.GRAVEL, Items.GRAVEL, Items.GRAVEL),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "gravel")]!!
        )).add(SCCSRecipe(
                // Leaves
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.OAK_LEAVES, Items.OAK_LEAVES, Items.OAK_LEAVES, Items.OAK_LEAVES),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "leaves")]!!
        )).add(SCCSRecipe(
                // No Gravity
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "no_gravity")]!!
        )).add(SCCSRecipe(
                // High Gravity
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.ANVIL, Items.BRICK, Items.BRICK, Items.BRICK),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "high_gravity")]!!
        )).add(SCCSRecipe(
                // Low Gravity
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.STRING, Items.STRING),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "low_gravity")]!!
        )).add(SCCSRecipe(
                // Reverse Gravity
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.FERMENTED_SPIDER_EYE, Items.FERMENTED_SPIDER_EYE),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "reverse_gravity")]!!
        )).add(SCCSRecipe(
                // Bounce
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.SLIME_BALL, Items.SLIME_BALL, Items.SLIME_BALL, Items.SLIME_BALL),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "bounce")]!!
        )).add(SCCSRecipe(
                // Target Ground
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.BOW, Items.BOW, Items.CROSSBOW, Items.CROSSBOW),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "target_ground")]!!
        )).add(SCCSRecipe(
                // Explode
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.TNT, Items.TNT, Items.TNT, Items.TNT),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "explode")]!!
        )).add(SCCSRecipe(
                // Large Explode
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(
                        VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "explode")]!!,
                        VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "explode")]!!,
                        VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "explode")]!!,
                        VeneMain.MAGIC_BINDING
                ),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "large_explode")]!!
        )).add(SCCSRecipe(
                // Create Force
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(
                        VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "explode")]!!,
                        VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "explode")]!!,
                        Items.WATER_BUCKET,
                        VeneMain.MAGIC_BINDING
                ),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "create_force")]!!
        )).add(SCCSRecipe(
                // Create Light
                VeneMain.EMPTY_SPELL_COMPONENT,
                mutableListOf(Items.TORCH, Items.LANTERN, Items.SEA_LANTERN, Items.GLOWSTONE),
                VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, "create_light")]!!)
        )
    }

    fun recipes(pack: RuntimeResourcePack) {
        pack.addRecipe(Identifier(MOD_ID, "sccs"), shaped(
                pattern(
                        " D ",
                        "POP",
                        "POP"
                ),
                keys()
                        .key("D",
                                ingredient()
                                        .item(Items.DIAMOND)
                        )
                        .key("O",
                                ingredient()
                                        .item(Items.OBSIDIAN)
                        ).key("P",
                                ingredient()
                                        .item(Items.OAK_PLANKS)
                        ),
                item(VeneMain.WAND_EDIT_BLOCK.asItem())
        ));
    }

    fun lootTables(pack: RuntimeResourcePack) {
        pack.addLootTable(Identifier(MOD_ID, "blocks/wand_edit"),
                loot("minecraft:block")
                        .pool(pool()
                                .rolls(1)
                                .entry(entry()
                                        .type("minecraft:item")
                                        .name(Identifier(MOD_ID, "wand_edit").toString()))
                                .condition(condition("minecraft:survives_explosion")))
        )

        pack.addLootTable(Identifier(MOD_ID, "blocks/sccs"),
                loot("minecraft:block")
                        .pool(pool()
                                .rolls(1)
                                .entry(entry()
                                        .type("minecraft:item")
                                        .name(Identifier(MOD_ID, "sccs").toString()))
                                .condition(condition("minecraft:survives_explosion")))
        )
    }

    fun blocks() {
        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "light"), VeneMain.LIGHT_BLOCK)
        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "wand_edit"), VeneMain.WAND_EDIT_BLOCK)
        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "sccs"), VeneMain.SCCS_BLOCK)
    }

    fun items() {
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "wand"), VeneMain.WAND_ITEM)
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "magic_binding"), VeneMain.MAGIC_BINDING)
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "empty_component"), VeneMain.EMPTY_SPELL_COMPONENT)
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "wand_edit"), BlockItem(VeneMain.WAND_EDIT_BLOCK, Item.Settings().group(VeneMain.ITEM_GROUP)))
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "sccs"), BlockItem(VeneMain.SCCS_BLOCK, Item.Settings().group(VeneMain.ITEM_GROUP)))
    }
}