/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.data

import com.oroarmor.multi_item_lib.UniqueItemRegistry
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
import net.minecraft.block.DispenserBlock
import net.minecraft.block.dispenser.DispenserBehavior
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.resource.ResourceType
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Property
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.vene.VeneMain
import net.vene.VeneMain.Companion.MAGIC_BINDING
import net.vene.VeneMain.Companion.MOD_ID
import net.vene.common.util.extension.cleanID
import net.vene.common.util.extension.formattedID
import net.vene.common.util.extension.id
import java.nio.charset.Charset
import java.util.*


// "Static" functions for init-ing data
// Most usages are in VeneMain
object StaticDataHandler {
    private val EMPTY_COMPONENT = VeneMain.EMPTY_SPELL_COMPONENT

    fun lang(lang: JLang) {
        lang.entry(
            "item.vene.wand",
            "Magic Wand"
        ).entry(
            "item.vene.magic_crossbow",
            "Magic Crossbow"
        ).entry(
            "item.vene.magic_binding",
            "Magic Binding"
        ).entry(
            "itemGroup.vene.items",
            "Veneficium Items"
        ).entry(
            "itemGroup.vene.components",
            "Veneficium Spell Components"
        ).entry(
            "vene.screen.wand_edit.title",
            "Edit Wands"
        ).entry(
            "vene.screen.compat.category.weaving",
            "Weaving"
        ).entry(
            "item.vene.empty_component",
            "Blank Spell Component"
        ).entry(
            "block.vene.sccs",
            "Weave Focus"
        ).entry(
            "block.vene.wand_edit",
            "Wand Edit Block"
        )
    }

    fun blockStates(pack: RuntimeResourcePack) {
        pack.addBlockState(state(variant(model("vene:block/light"))), Identifier(MOD_ID, "light"))
        pack.addBlockState(state(variant(model("vene:block/sccs"))), Identifier(MOD_ID, "sccs"))
        pack.addBlockState(state(variant(model("vene:block/wand_edit"))), Identifier(MOD_ID, "wand_edit"))
    }

    fun sccsRecipes(pack: RuntimeResourcePack) {
        RecipeBuilder(pack) {
            /**
             * Other
             */
            // Magic crossbow
            addOther(Items.CROSSBOW, listOf(VeneMain.WAND_ITEM, MAGIC_BINDING, spellComponent("low_gravity"), spellComponent("low_gravity")), VeneMain.MAGIC_CROSSBOW_ITEM)

            /**
             * Cosmetic
             */
            // No trail
            add(EMPTY_COMPONENT, listOf(Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE), spellComponent("no_trail"))
            // Normal trail
            add(
                spellComponent("no_trail"),
                listOf(Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET),
                spellComponent("normal_trail")
            )
            // Fire trail
            add(
                spellComponent("no_trail"),
                listOf(Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.TORCH, Items.FLINT),
                spellComponent("fire_trail")
            )
            // Soul fire trail
            add(
                spellComponent("no_trail"),
                listOf(Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.SOUL_TORCH, Items.SOUL_CAMPFIRE),
                spellComponent("soul_fire_trail")
            )
            // Heart trail
            add(
                spellComponent("no_trail"),
                listOf(Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.EGG, Items.EGG),
                spellComponent("heart_trail")
            )
            // Magic trail
            add(
                spellComponent("no_trail"),
                listOf(Items.FIREWORK_ROCKET, Items.FIREWORK_ROCKET, Items.BREWING_STAND, Items.DRAGON_BREATH),
                spellComponent("magic_trail")
            )

            /**
             * Material
             */
            // Water
            add(
                EMPTY_COMPONENT,
                listOf(Items.WATER_BUCKET, Items.WATER_BUCKET, Items.WATER_BUCKET, Items.WATER_BUCKET),
                spellComponent("water")
            )
            // Cobblestone
            add(
                EMPTY_COMPONENT,
                listOf(Items.WATER_BUCKET, Items.LAVA_BUCKET, Items.WOODEN_PICKAXE, Items.CLOCK),
                spellComponent("cobblestone")
            )
            // Dirt
            add(
                EMPTY_COMPONENT,
                listOf(Items.DIRT, Items.DIRT, Items.COARSE_DIRT, Items.PODZOL),
                spellComponent("dirt")
            )
            // Gravel
            add(
                EMPTY_COMPONENT,
                listOf(Items.GRAVEL, Items.GRAVEL, Items.GRAVEL, Items.GRAVEL),
                spellComponent("gravel")
            )

            /**
             * Move
             */
            // No gravity
            add(
                EMPTY_COMPONENT,
                listOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE),
                spellComponent("no_gravity")
            )
            // High gravity
            add(
                EMPTY_COMPONENT,
                listOf(Items.ANVIL, Items.BRICK, Items.BRICK, Items.BRICK),
                spellComponent("high_gravity")
            )
            // Low gravity
            add(
                EMPTY_COMPONENT,
                listOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.STRING, Items.STRING),
                spellComponent("low_gravity")
            )
            // Reverse gravity
            add(
                EMPTY_COMPONENT,
                listOf(Items.PHANTOM_MEMBRANE, Items.PHANTOM_MEMBRANE, Items.FERMENTED_SPIDER_EYE, Items.FERMENTED_SPIDER_EYE),
                spellComponent("reverse_gravity")
            )
            // Bounce
            add(
                EMPTY_COMPONENT,
                listOf(Items.SLIME_BALL, Items.SLIME_BALL, Items.SLIME_BALL, Items.SLIME_BALL),
                spellComponent("bounce")
            )
            // Slow down
            add(
                EMPTY_COMPONENT,
                listOf(Items.COBWEB, Items.SOUL_SAND, Items.HONEY_BLOCK, Items.SCUTE),
                spellComponent("slow_down")
            )
            // Speed up
            add(EMPTY_COMPONENT, listOf(Items.SUGAR, Items.SUGAR, Items.SUGAR, Items.SUGAR), spellComponent("speed_up"))
            // Accelerate
            add(
                EMPTY_COMPONENT,
                listOf(Items.SUGAR, Items.SUGAR, spellComponent("speed_up"), spellComponent("wait_0.05_seconds")),
                spellComponent("accelerate")
            )

            /**
             * Result
             */
            // Target ground
            add(
                EMPTY_COMPONENT,
                listOf(Items.BOW, Items.BOW, Items.CROSSBOW, Items.CROSSBOW),
                spellComponent("target_ground")
            )
            // Target entity
            add(
                EMPTY_COMPONENT,
                listOf(Items.GUNPOWDER, Items.ROTTEN_FLESH, MAGIC_BINDING, spellComponent("target_ground")),
                spellComponent("target_entity")
            )
            // Target entity or ground
            add(
                EMPTY_COMPONENT,
                listOf(MAGIC_BINDING, MAGIC_BINDING, spellComponent("target_entity"), spellComponent("target_ground")),
                spellComponent("target_entity_or_ground")
            )
            // Target current
            add(
                EMPTY_COMPONENT,
                listOf(Items.ARROW, Items.ARROW, Items.REDSTONE, Items.REDSTONE),
                spellComponent("target_current")
            )
            // Damage entity
            add(
                EMPTY_COMPONENT,
                listOf(Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD),
                spellComponent("damage_entity")
            )
            // Explode
            add(EMPTY_COMPONENT, listOf(Items.TNT, Items.TNT, Items.TNT, Items.TNT), spellComponent("explode"))
            // Large explode
            add(
                EMPTY_COMPONENT,
                listOf(spellComponent("explode"), spellComponent("explode"), spellComponent("explode"), MAGIC_BINDING),
                spellComponent("large_explode")
            )
            // Create force
            add(
                EMPTY_COMPONENT,
                listOf(spellComponent("explode"), spellComponent("explode"), Items.WATER_BUCKET, MAGIC_BINDING),
                spellComponent("create_force")
            )
            // Create light
            add(
                EMPTY_COMPONENT,
                listOf(Items.TORCH, Items.LANTERN, Items.SEA_LANTERN, Items.GLOWSTONE),
                spellComponent("create_light")
            )
            // Freeze
            add(
                EMPTY_COMPONENT,
                listOf(Items.PACKED_ICE, Items.SNOW_BLOCK, spellComponent("slow_down"), spellComponent("slow_down")),
                spellComponent("freeze")
            )
            // Wait 0.05 seconds
            add(
                EMPTY_COMPONENT,
                listOf(Items.CLOCK, Items.CLOCK, Items.REPEATER, Items.REPEATER),
                spellComponent("wait_0.05_seconds")
            )
            // Wait 0.15 seconds
            add(
                spellComponent("wait_0.05_seconds"),
                listOf(spellComponent("wait_0.05_seconds"), spellComponent("wait_0.05_seconds"), MAGIC_BINDING, MAGIC_BINDING),
                spellComponent("wait_0.15_seconds")
            )
            // Wait 0.25 seconds
            add(
                spellComponent("wait_0.15_seconds"),
                listOf(spellComponent("wait_0.05_seconds"), spellComponent("wait_0.05_seconds"), MAGIC_BINDING, MAGIC_BINDING),
                spellComponent("wait_0.25_seconds")
            )
            // Wait 0.5 seconds
            add(
                spellComponent("wait_0.25_seconds"),
                listOf(spellComponent("wait_0.25_seconds"), Items.CLOCK, MAGIC_BINDING, MAGIC_BINDING),
                spellComponent("wait_0.5_seconds")
            )
            // Wait 0.5 seconds
            add(
                spellComponent("wait_0.5_seconds"),
                listOf(spellComponent("wait_0.5_seconds"), Items.CLOCK, MAGIC_BINDING, MAGIC_BINDING),
                spellComponent("wait_1.0_seconds")
            )
            // Chance 50
            add(
                EMPTY_COMPONENT,
                listOf(Items.EGG, Items.DISPENSER, Items.DROPPER, Items.WHEAT_SEEDS),
                spellComponent("chance_50")
            )
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
                            .item(MAGIC_BINDING)
                    ),
                item(VeneMain.WAND_ITEM)
            )
        )

        pack.addRecipe(
            Identifier(MOD_ID, "infused_stick"), shaped(
                pattern(
                    " PS",
                    " S ",
                    "SP "
                ),
                keys()
                    .key(
                        "S",
                        ingredient()
                            .item(Items.STICK)
                    ).key(
                        "P",
                        ingredient()
                            .item(Items.ENDER_PEARL)
                    ),
                item(VeneMain.INFUSED_STICK)
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
                JResult.itemStack(MAGIC_BINDING, 1)
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
                        .condition(predicate("minecraft:survives_explosion"))
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
                        .condition(predicate("minecraft:survives_explosion"))
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
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "magic_crossbow"), VeneMain.MAGIC_CROSSBOW_ITEM)
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "infused_stick"), VeneMain.INFUSED_STICK)
        UniqueItemRegistry.CROSSBOW.addItemToRegistry(VeneMain.MAGIC_CROSSBOW_ITEM)
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "magic_binding"), MAGIC_BINDING)
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

    fun dispenserBehaviors() {
        DispenserBlock.registerBehavior(VeneMain.WAND_ITEM) { pointer, stack ->
            val facing = pointer.blockState[DispenserBlock.FACING].vector
            val blockPos = pointer.blockPos.add(facing)
            val firing = Vec3d.ofCenter(blockPos).subtract(facing.x / 4.0, facing.y / 4.0, facing.z / 4.0)
            println(firing)
            stack
        }
    }

    fun spellComponent(name: String): Item {
        return VeneMain.SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, name)]!!
    }

    private class RecipeBuilder(val pack: RuntimeResourcePack, lambda: RecipeBuilder.() -> Unit) {
        init {
            lambda()
        }

        fun add(core: Item, ingredients: List<Item>, result: Item) {
            val output = buildString {
                append("{\"type\":\"vene:sccs\",")
                append("\"core\":${core.formattedID()},")
                append("\"ingredients\": [")
                ingredients.forEachIndexed { i: Int, item: Item ->
                    append(if (ingredients.lastIndex != i) item.formattedID() + "," else item.formattedID())
                }
                append("],")
                append("\"result\":${result.formattedID()}")
                append("}")
            }
            pack.addResource(ResourceType.SERVER_DATA, Identifier(MOD_ID, "recipes/components/${result.cleanID().split("/").last()}.json"), output.toByteArray(Charset.forName("UTF-8")))
        }

        fun addOther(core: Item, ingredients: List<Item>, result: Item) {
            val output = buildString {
                append("{\"type\":\"vene:sccs\",")
                append("\"core\":${core.formattedID()},")
                append("\"ingredients\": [")
                ingredients.forEachIndexed { i: Int, item: Item ->
                    append(if (ingredients.lastIndex != i) item.formattedID() + "," else item.formattedID())
                }
                append("],")
                append("\"result\":${result.formattedID()}")
                append("}")
            }
            pack.addResource(ResourceType.SERVER_DATA, Identifier(MOD_ID, "recipes/${result.id().path}.json"), output.toByteArray(Charset.forName("UTF-8")))
        }
    }
}
