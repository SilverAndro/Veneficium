/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.init

import net.devtech.arrp.api.RuntimeResourcePack
import net.devtech.arrp.json.blockstate.JState.*
import net.devtech.arrp.json.lang.JLang
import net.devtech.arrp.json.loot.JLootTable.*
import net.devtech.arrp.json.recipe.JIngredient.ingredient
import net.devtech.arrp.json.recipe.JIngredients.ingredients
import net.devtech.arrp.json.recipe.JKeys.keys
import net.devtech.arrp.json.recipe.JPattern.pattern
import net.devtech.arrp.json.recipe.JRecipe
import net.devtech.arrp.json.recipe.JRecipe.shaped
import net.devtech.arrp.json.recipe.JResult
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
    private val EMPTY_COMPONENT = VeneMain.EMPTY_SPELL_COMPONENT

    fun lang(lang: JLang) {
        lang.translate(
            "item.vene.wand",
            "Magic Wand"
        ).translate(
            "item.vene.magic_binding",
            "Magic Binding"
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

    fun blockStates(pack: RuntimeResourcePack) {
        pack.addBlockState(state(variant(model("vene:block/light"))), Identifier(MOD_ID, "light"))
        pack.addBlockState(state(variant(model("vene:block/sccs"))), Identifier(MOD_ID, "sccs"))
        pack.addBlockState(state(variant(model("vene:block/wand_edit"))), Identifier(MOD_ID, "wand_edit"))
    }

    fun componentRecipes(recipes: SCCSRecipeList) {
        RecipeBuilder(recipes) {
            /**
             * Cosmetic
             */
            // No trail
            add(spellComponent("no_trail"), EMPTY_COMPONENT, listOf(Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE))
            // Normal trail
            add(spellComponent("normal_trail"), spellComponent("no_trail"), listOf(Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET))
            // Fire trail
            add(spellComponent("fire_trail"), spellComponent("no_trail"), listOf(Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.TORCH, Items.FLINT))

            /**
             * Material
             */
            // Water
            add(spellComponent("water"), EMPTY_COMPONENT, listOf(Items.WATER_BUCKET, Items.WATER_BUCKET, Items.WATER_BUCKET, Items.WATER_BUCKET))
            // Cobblestone
            add(spellComponent("cobblestone"), EMPTY_COMPONENT, listOf(Items.WATER_BUCKET, Items.LAVA_BUCKET, Items.WOODEN_PICKAXE, Items.CLOCK))
            // Dirt
            add(spellComponent("dirt"), EMPTY_COMPONENT, listOf(Items.DIRT, Items.DIRT, Items.COARSE_DIRT, Items.PODZOL))
            // Gravel
            add(spellComponent("gravel"), EMPTY_COMPONENT, listOf(Items.GRAVEL, Items.GRAVEL, Items.GRAVEL, Items.GRAVEL))

            /**
             * Move
             */
            // No gravity
            add(spellComponent("no_gravity"), EMPTY_COMPONENT, listOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE))
            // High gravity
            add(spellComponent("high_gravity"), EMPTY_COMPONENT, listOf(Items.ANVIL, Items.BRICK, Items.BRICK, Items.BRICK))
            // Low gravity
            add(spellComponent("low_gravity"), EMPTY_COMPONENT, listOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.STRING, Items.STRING))
            // Reverse gravity
            add(spellComponent("reverse_gravity"), EMPTY_COMPONENT, listOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.FERMENTED_SPIDER_EYE, Items.FERMENTED_SPIDER_EYE))
            // Bounce
            add(spellComponent("bounce"), EMPTY_COMPONENT, listOf(Items.SLIME_BALL, Items.SLIME_BALL, Items.SLIME_BALL, Items.SLIME_BALL))
            // Slow down
            add(spellComponent("slow_down"), EMPTY_COMPONENT, listOf(Items.COBWEB, Items.SOUL_SAND, Items.HONEY_BLOCK, Items.SCUTE))
            // Speed up
            add(spellComponent("speed_up"), EMPTY_COMPONENT, listOf(Items.SUGAR, Items.SUGAR, Items.SUGAR, Items.SUGAR))
            // Accelerate
            add(spellComponent("accelerate"), EMPTY_COMPONENT, listOf(Items.SUGAR, Items.SUGAR, spellComponent("speed_up"), spellComponent("wait_0.05_seconds")))

            /**
             * Result
             */
            // Target ground
            add(spellComponent("target_ground"), EMPTY_COMPONENT, listOf(Items.BOW, Items.BOW, Items.CROSSBOW, Items.CROSSBOW))
            // Target entity
            add(spellComponent("target_entity"), EMPTY_COMPONENT, listOf(Items.GUNPOWDER, Items.ROTTEN_FLESH, VeneMain.MAGIC_BINDING, spellComponent("target_ground")))
            // Target entity or ground
            add(spellComponent("target_entity_or_ground"), EMPTY_COMPONENT, listOf(VeneMain.MAGIC_BINDING, VeneMain.MAGIC_BINDING, spellComponent("target_entity"), spellComponent("target_ground")))
            // Explode
            add(spellComponent("explode"), EMPTY_COMPONENT, listOf(Items.TNT, Items.TNT, Items.TNT, Items.TNT))
            // Large explode
            add(spellComponent("large_explode"), EMPTY_COMPONENT, listOf(spellComponent("explode"), spellComponent("explode"), spellComponent("explode"), VeneMain.MAGIC_BINDING))
            // Create force
            add(spellComponent("create_force"), EMPTY_COMPONENT, listOf(spellComponent("explode"), spellComponent("explode"), Items.WATER_BUCKET, VeneMain.MAGIC_BINDING))
            // Create light
            add(spellComponent("create_light"), EMPTY_COMPONENT, listOf(Items.TORCH, Items.LANTERN, Items.SEA_LANTERN, Items.GLOWSTONE))

            finalize()
        }
    }

    fun recipes(pack: RuntimeResourcePack) {
        pack.addRecipe(
            Identifier(MOD_ID, "wand_edit"), shaped(
                pattern(
                    " D ",
                    "POP",
                    "POP"
                ),
                keys()
                    .key(
                        "D",
                        ingredient()
                            .item(Items.DIAMOND)
                    )
                    .key(
                        "O",
                        ingredient()
                            .item(Items.OBSIDIAN)
                    ).key(
                        "P",
                        ingredient()
                            .item(Items.OAK_PLANKS)
                    ),
                item(VeneMain.WAND_EDIT_BLOCK.asItem())
            )
        )

        pack.addRecipe(
            Identifier(MOD_ID, "sccs"), shaped(
                pattern(
                    "OOO",
                    " Q ",
                    "QQQ"
                ),
                keys()
                    .key(
                        "O",
                        ingredient()
                            .item(Items.OBSIDIAN)
                    ).key(
                        "Q",
                        ingredient()
                            .item(Items.QUARTZ_PILLAR)
                    ),
                item(VeneMain.SCCS_BLOCK.asItem())
            )
        )

        pack.addRecipe(
            Identifier(MOD_ID, "empty_component"), shaped(
                pattern(
                    "PPP",
                    "PIP",
                    "PPP"
                ),
                keys()
                    .key(
                        "P",
                        ingredient()
                            .item(Items.OAK_PLANKS)
                    ).key(
                        "I",
                        ingredient()
                            .item(Items.ITEM_FRAME)
                    ),
                item(VeneMain.EMPTY_SPELL_COMPONENT)
            )
        )

        pack.addRecipe(
            Identifier(MOD_ID, "wand"), shaped(
                pattern(
                    " BC",
                    " PB",
                    "P  "
                ),
                keys()
                    .key(
                        "P",
                        ingredient()
                            .item(Items.OAK_PLANKS)
                    ).key(
                        "C",
                        ingredient()
                            .item(Items.CRYING_OBSIDIAN)
                    ).key(
                        "B",
                        ingredient()
                            .item(VeneMain.MAGIC_BINDING)
                    ),
                item(VeneMain.WAND_ITEM)
            )
        )

        pack.addRecipe(
            Identifier(MOD_ID, "magic_binding"), JRecipe.shapeless(
                ingredients()
                    .add(
                        ingredient()
                            .item(Items.MAGMA_CREAM)
                    )
                    .add(
                        ingredient()
                            .item(Items.STRING)
                    )
                    .add(
                        ingredient()
                            .item(Items.HONEYCOMB)
                    ),
                JResult.itemStack(VeneMain.MAGIC_BINDING, 1)
            )
        )
    }

    fun lootTables(pack: RuntimeResourcePack) {
        pack.addLootTable(
            Identifier(MOD_ID, "blocks/wand_edit"),
            loot("minecraft:block")
                .pool(
                    pool()
                        .rolls(1)
                        .entry(
                            entry()
                                .type("minecraft:item")
                                .name(Identifier(MOD_ID, "wand_edit").toString())
                        )
                        .condition(condition("minecraft:survives_explosion"))
                )
        )

        pack.addLootTable(
            Identifier(MOD_ID, "blocks/sccs"),
            loot("minecraft:block")
                .pool(
                    pool()
                        .rolls(1)
                        .entry(
                            entry()
                                .type("minecraft:item")
                                .name(Identifier(MOD_ID, "sccs").toString())
                        )
                        .condition(condition("minecraft:survives_explosion"))
                )
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
        Registry.register(
            Registry.ITEM,
            Identifier(MOD_ID, "wand_edit"),
            BlockItem(VeneMain.WAND_EDIT_BLOCK, Item.Settings().group(VeneMain.ITEM_GROUP))
        )
        Registry.register(
            Registry.ITEM,
            Identifier(MOD_ID, "sccs"),
            BlockItem(VeneMain.SCCS_BLOCK, Item.Settings().group(VeneMain.ITEM_GROUP))
        )
    }

    private fun spellComponent(name: String): Item {
        return VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, name)]!!
    }

    private class RecipeBuilder(val recipes: SCCSRecipeList, lambda: RecipeBuilder.() -> Unit) {
        private val newRecipes: MutableList<SCCSRecipe> = mutableListOf()

        init {
            lambda()
        }

        fun add(result: Item, core: Item, ingredients: List<Item>) {
            newRecipes.add(SCCSRecipe(core, ingredients.toMutableList(), result))
        }

        fun finalize() {
            newRecipes.forEach {
                recipes.add(it)
            }
        }
    }
}
