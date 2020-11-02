/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.block.entity

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.fabricmc.fabric.api.server.PlayerStream
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BeaconBlockEntity.BeamSegment
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LightningEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.Heightmap
import net.vene.VeneMain
import net.vene.common.block.SCCSBlock

class SCCSBlockEntity : BlockEntity(VeneMain.SCCS_BLOCK_ENTITY), Tickable {
    var canWork = false
    var isWorking = false
    var toCraftResult: Item = Items.AIR
    var workingTimeRemaining = -1

    var beamSegments: MutableList<BeamSegment> = mutableListOf()

    var heldItemStack: ItemStack = ItemStack(Items.AIR)
    var lockItem = false

    var isRoot = false

    private val color = floatArrayOf(1f, 1f, 1f)

    override fun tick() {
        // Detect for beam
        val height = world!!.getTopY(Heightmap.Type.WORLD_SURFACE, pos.x, pos.z)
        canWork = height <= pos.y + 1

        // Get rid of beam if we dont need it
        if (!canWork || !isWorking) {
            beamSegments.clear()
        }

        // Add a beam segment
        if (canWork && isWorking && beamSegments.size <= 1) {
            val beamSegment = BeamSegment(color)
            beamSegments.add(beamSegment)
        }

        // If we are client or we don't have a world somehow
        if (world?.isClient != false) {
            return
        }

        // In case I forget to sync somewhere, this will force a sync every 2 seconds
        if (world!!.time % 40 == (0).toLong()) {
            sendUpdateStatus()
        }

        // Get blocks at expected positions
        val states: MutableMap<BlockPos, BlockState> = mutableMapOf()
        states[pos.add(2, 0, 2)] = world!!.getBlockState(pos.add(2, 0, 2))
        states[pos.add(-2, 0, 2)] = world!!.getBlockState(pos.add(-2, 0, 2))
        states[pos.add(2, 0, -2)] = world!!.getBlockState(pos.add(2, 0, -2))
        states[pos.add(-2, 0, -2)] = world!!.getBlockState(pos.add(-2, 0, -2))

        // Count up the valid blocks
        var count = 0
        for (state in states.values) {
            if (state.block is SCCSBlock) {
                count++
            } else {
                break
            }
        }

        // We are root
        if (count == 4) {
            isRoot = true

            // Convert positions to block entities (We know their safe because we just counted them)
            val blockEntitiesInSystem: MutableList<SCCSBlockEntity> = mutableListOf()
            for (pos in states.keys) {
                blockEntitiesInSystem.add(world!!.getBlockEntity(pos) as SCCSBlockEntity)
            }

            // Collect the ingredients from the other pillars
            val ingredients: MutableList<Item> = mutableListOf()
            for (entity in blockEntitiesInSystem) {
                if (entity.canWork) {
                    ingredients.add(entity.heldItemStack.item)
                }
            }

            // If we can make something from this (and we arent already making something)
            if (VeneMain.SCCS_RECIPES.coreHasRecipe(heldItemStack.item) && !isWorking) {
                // Cache the result
                toCraftResult = VeneMain.SCCS_RECIPES.craft(heldItemStack.item, ingredients)

                // If its a valid result and we can make something
                if (toCraftResult != Items.AIR && canWork) {

                    // Display beam and dont allow taking out the item (for all pillars)
                    isWorking = true
                    lockItem = true
                    sendUpdateStatus()
                    for (entity in blockEntitiesInSystem) {
                        entity.isWorking = true
                        entity.lockItem = true
                        entity.sendUpdateStatus()
                    }

                    // Play a sound effect
                    val watching = PlayerStream.watching(this)
                    for (player in watching) {
                        (player as ServerPlayerEntity).networkHandler.sendPacket(PlaySoundIdS2CPacket(
                                SoundEvents.ENTITY_ENDER_EYE_DEATH.id,
                                SoundCategory.BLOCKS,
                                Vec3d(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5),
                                2.2F,
                                0.8F)
                        )
                    }

                    // Start the working countdown
                    workingTimeRemaining = 60
                }
            }

            // Currently making something
            if (isWorking) {
                // Check to make sure someone hasnt broken a pillar
                val newBlockEntities: MutableList<SCCSBlockEntity> = mutableListOf()
                for (pos in states.keys) {
                    val result = world!!.getBlockEntity(pos)

                    if (result is SCCSBlockEntity) {
                        newBlockEntities.add(result)
                    }
                }

                // All the pillars are still there, onwards!
                if (newBlockEntities.containsAll(blockEntitiesInSystem)) {
                    // If we are done crafting
                    if (workingTimeRemaining == 0) {
                        // Create a lightning strike
                        val lightning = EntityType.LIGHTNING_BOLT.create(world)!!
                        lightning.refreshPositionAfterTeleport(pos.x + 0.5, pos.y + 0.2, pos.z + 0.5)
                        world!!.spawnEntity(lightning)

                        // Set the output to the result
                        heldItemStack = ItemStack(toCraftResult)

                        // Stop beam and let items be taken out again (for us and all pillars)
                        isWorking = false
                        lockItem = false
                        sendUpdateStatus()
                        for (entity in blockEntitiesInSystem) {
                            entity.heldItemStack = ItemStack(entity.heldItemStack.item.recipeRemainder ?: Items.AIR)
                            entity.isWorking = false
                            entity.lockItem = false
                            entity.sendUpdateStatus()
                        }

                        // Disable crafting
                        workingTimeRemaining = -1
                    }

                    // Still counting down
                    if (workingTimeRemaining > 0) {
                        workingTimeRemaining--
                        // Display some particles
                        world!!.players.forEach {
                            (world as ServerWorld).spawnParticles(it as ServerPlayerEntity, ParticleTypes.ENCHANT, true, pos.x + 0.5, pos.y + 1.4, pos.z + 0.5, 7, 0.0, 0.0, 0.0, 1.0)
                        }
                    }
                }
            }
        // We lost 1 or more pillars (or arent root)
        } else {
            // If we were root, we need to stop crafting
            if (isRoot) {
                isRoot = false

                // Unlock everything, disable beam, yada yada
                isWorking = false
                lockItem = false
                workingTimeRemaining = -1
                sendUpdateStatus()

                for (pos in states.keys) {
                    val result = world!!.getBlockEntity(pos)
                    if (result is SCCSBlockEntity) {
                        result.isWorking = false
                        result.lockItem = false
                        result.sendUpdateStatus()
                    }
                }
            }
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.put("item", heldItemStack.toTag(CompoundTag()))
        return super.toTag(tag)
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        try {
            val item = tag.get("item") as CompoundTag
            heldItemStack = ItemStack.fromTag(item)
        } catch (npe: NullPointerException) {
            // Doesn't work on client for some reason, will get synced in 2 seconds
        }
    }

    fun sendUpdateStatus() {
        // Client isn't allowed to sync (also syncing without a world)
        if (world?.isClient != false) {
            return
        }
        // Send held item and if we are working
        val watchingPlayers = PlayerStream.watching(this)
        val passedData = PacketByteBuf(Unpooled.buffer())
        passedData.writeBlockPos(pos)
        passedData.writeItemStack(heldItemStack)
        passedData.writeBoolean(isWorking)
        watchingPlayers.forEach { ServerSidePacketRegistry.INSTANCE.sendToPlayer(it, VeneMain.UPDATE_HELD_ITEM, passedData) }
    }

    override fun onSyncedBlockEvent(type: Int, data: Int): Boolean {
        sendUpdateStatus()
        return super.onSyncedBlockEvent(type, data)
    }
}