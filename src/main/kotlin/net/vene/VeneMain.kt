/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene

import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3
import net.devtech.arrp.api.RRPCallback
import net.devtech.arrp.api.RuntimeResourcePack
import net.devtech.arrp.json.lang.JLang.lang
import net.devtech.arrp.json.models.JModel
import net.devtech.arrp.json.models.JTextures
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.*
import net.minecraft.resource.ResourcePack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.vene.cca_component.WandSpellsComponent
import net.vene.common.block.LightBlock
import net.vene.common.block.SCCSBlock
import net.vene.common.block.WandEditBlock
import net.vene.common.block.entity.SCCSBlockEntity
import net.vene.common.block.entity.WandEditBlockEntity
import net.vene.common.item.ComponentItem
import net.vene.common.item.WandItem
import net.vene.common.magic.SpellExecutor
import net.vene.common.magic.spell_components.MaterialComponent
import net.vene.common.magic.spell_components.MoveComponent
import net.vene.common.magic.spell_components.ResultComponent
import net.vene.common.magic.spell_components.collection.MaterialComponents
import net.vene.common.magic.spell_components.collection.MoveComponents
import net.vene.common.magic.spell_components.collection.ResultComponents
import net.vene.common.magic.util.StringUtil
import net.vene.common.screen.WandEditScreenHandler
import net.vene.init.StaticDataAdder
import net.vene.recipe.SCCSRecipeList
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


@Suppress("MemberVisibilityCanBePrivate")
class VeneMain : ModInitializer {
    override fun onInitialize() {
        LOGGER.info("Ecce!")

        // Forcibly loads the component lists
        // Try to remove in future
        MaterialComponents
        MoveComponents
        ResultComponents

        LOGGER.debug("Registering RRP")
        val lang = lang()
        RRPCallback.EVENT.register(RRPCallback { a: MutableList<ResourcePack> -> a.add(RESOURCE_PACK) })

        LOGGER.debug("Registering And Generating Items")
        StaticDataAdder.items()
        // Dynamically create component items
        // This is the most blessed code ive ever written

        // Combine all lists of components into one iterable
        for (component in MATERIAL_COMPONENTS union MOVE_COMPONENTS union RESULT_COMPONENTS) {
            // Create an item for the component
            val item = ComponentItem(Item.Settings().group(ITEM_GROUP_COMPONENTS).maxCount(1), component)
            // Register the item
            Registry.register(
                    Registry.ITEM,
                    Identifier(MOD_ID, "${component.type.toString().toLowerCase()}/${component.name}"),
                    item
            )

            // Put it into the big component list
            SPELL_COMPONENT_ITEMS[Identifier(MOD_ID, component.name)] = item

            // Add translation
            lang.translate(
                    "item.vene.${component.type.toString().toLowerCase()}.${component.name}",
                    StringUtil.displayFromUnderscored(component.name)
            )

            // Auto model so no json handling for new components
            RESOURCE_PACK.addModel(JModel.model("item/generated").textures(JTextures().layer0("vene:item/${component.type.toString().toLowerCase()}/${component.name}")), Identifier(MOD_ID, "item/${component.type.toString().toLowerCase()}/${component.name}"))
        }

        LOGGER.debug("Registering Blocks")
        StaticDataAdder.blocks()

        LOGGER.debug("Registering event listeners")
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvents.EndTick {
            for (executor in ACTIVE_SPELLS) {
                executor.tick()
            }
            for (removed in SPELLS_TO_BE_REMOVED) {
                ACTIVE_SPELLS.remove(removed)
            }
            SPELLS_TO_BE_REMOVED.clear()
        })

        LOGGER.debug("Adding Extra lang entries to lang")
        StaticDataAdder.lang(lang)

        LOGGER.debug("Adding files to RRP")
        RESOURCE_PACK.addLang(Identifier(MOD_ID, "en_us"), lang)

        LOGGER.debug("Adding recipes")
        StaticDataAdder.componentRecipes(SCCS_RECIPES)
        StaticDataAdder.recipes(RESOURCE_PACK)

        LOGGER.debug("Adding loot tables")
        StaticDataAdder.lootTables(RESOURCE_PACK)

        LOGGER.debug("MaterialComponents contains ${MATERIAL_COMPONENTS.size} entries")
        LOGGER.debug("MoveComponents contains ${MOVE_COMPONENTS.size} entries")
        LOGGER.debug("ResultComponents contains ${RESULT_COMPONENTS.size} entries")

        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            RESOURCE_PACK.dump()
        }
    }

    companion object {
        // Logging
        var LOGGER: Logger = LogManager.getLogger("Veneficium")

        // Mod ID
        const val MOD_ID = "vene"

        // RRP
        val RESOURCE_PACK: RuntimeResourcePack = RuntimeResourcePack.create(Identifier(MOD_ID, "rrp").toString())

        // Arrays so I don't have to rely on dirty reflection later (also maybe compatibility)
        val MATERIAL_COMPONENTS: MutableList<MaterialComponent> = mutableListOf()
        val MOVE_COMPONENTS: MutableList<MoveComponent> = mutableListOf()
        val RESULT_COMPONENTS: MutableList<ResultComponent> = mutableListOf()

        // Item groups
        val ITEM_GROUP: ItemGroup = FabricItemGroupBuilder.create(Identifier(MOD_ID, "items"))
                .icon { ItemStack(WAND_ITEM) }
                .build()
        val ITEM_GROUP_COMPONENTS: ItemGroup = FabricItemGroupBuilder.create(Identifier(MOD_ID, "components"))
                .icon { ItemStack(Items.BOOK) }
                .build()

        // Items
        val WAND_ITEM: WandItem = WandItem(Item.Settings().group(ITEM_GROUP).maxCount(1))
        val EMPTY_SPELL_COMPONENT = Item(Item.Settings().group(ITEM_GROUP_COMPONENTS).maxCount(1))
        val MAGIC_BINDING = Item(Item.Settings().group(ITEM_GROUP))
        val SPELL_COMPONENT_ITEMS: MutableMap<Identifier, ComponentItem> = mutableMapOf()

        // Blocks
        val LIGHT_BLOCK: Block = LightBlock(
                FabricBlockSettings
                        .of(Material.PORTAL)
                        .hardness(0.0f)
                        .collidable(false)
                        .lightLevel(15)
                        .resistance(0.0f)
                        .dropsNothing()
                        .nonOpaque()
                        .allowsSpawning { _, _, _, _ -> false }
        )
        val WAND_EDIT_BLOCK = WandEditBlock(FabricBlockSettings.of(Material.WOOD).nonOpaque().breakByTool(FabricToolTags.AXES).breakByHand(true).requiresTool().resistance(1.0f).hardness(1.0f).strength(1.0f))
        val SCCS_BLOCK = SCCSBlock(FabricBlockSettings.of(Material.METAL).nonOpaque().breakByTool(FabricToolTags.PICKAXES, 1).breakByHand(false).requiresTool().resistance(2.0f).hardness(2.0f).strength(2.0f))

        // Block Entities
        var WAND_EDIT_BLOCK_ENTITY: BlockEntityType<WandEditBlockEntity> = Registry.register(Registry.BLOCK_ENTITY_TYPE, Identifier(MOD_ID, "wand_edit"), BlockEntityType.Builder.create(::WandEditBlockEntity, WAND_EDIT_BLOCK).build(null))
        var SCCS_BLOCK_ENTITY: BlockEntityType<SCCSBlockEntity> = Registry.register(Registry.BLOCK_ENTITY_TYPE, Identifier(MOD_ID, "sccs"), BlockEntityType.Builder.create(::SCCSBlockEntity, SCCS_BLOCK).build(null))

        // Screen Handlers
        val BOX_SCREEN_HANDLER: ScreenHandlerType<WandEditScreenHandler> = ScreenHandlerRegistry.registerSimple(Identifier(MOD_ID, "wand_edit"), ::WandEditScreenHandler);

        // Components
        val WAND_SPELLS_COMPONENT: ComponentKey<WandSpellsComponent> = ComponentRegistryV3.INSTANCE.getOrCreate(Identifier(MOD_ID, "spell_list"), WandSpellsComponent::class.java)

        // PacketIDs
        val UPDATE_HELD_ITEM: Identifier = Identifier(MOD_ID, "update_held_item")

        // Misc
        val ACTIVE_SPELLS: MutableList<SpellExecutor> = mutableListOf()
        val SPELLS_TO_BE_REMOVED: MutableList<SpellExecutor> = mutableListOf()
        val SCCS_RECIPES = SCCSRecipeList()
    }
}