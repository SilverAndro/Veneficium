/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package net.vene.common.magic

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.vene.VeneMain
import net.vene.common.magic.event.EventInstance
import net.vene.common.magic.handling.SpellQueue
import net.vene.common.util.math.MathUtil
import net.vene.common.util.math.VectorIterator
import net.vene.mixin.CollidableAccessorMixin
import kotlin.random.Random

// This is basically a projectile but implemented as only math and particles because the entity system pains me
class SpellExecutor(private val owner: PlayerEntity, private val world: ServerWorld, var pos: Vec3d, var velocity : Vec3d, private val queue: SpellQueue) {
    // I don't remember why this is lazy, but it was probably important
    private val context by lazy {
        SpellContext(world, SpellContext.SpellCaster(owner, owner.pos, velocity), this)
    }

    var lifetime = 20 * 7
    var gravity = 0.03

    val events = EventInstance()

    fun tick() {
        queue.run(context)
        physics()
        events.moveTick.fire(context)
    }

    private fun display() {
        world.players.forEach {
            world.spawnParticles(it, ParticleTypes.ENCHANTED_HIT, true, pos.x, pos.y, pos.z, 1, 0.03, 0.03, 0.03, 0.0)
        }
    }

    private var lastChecked: BlockPos? = null
    private fun physics() {
        val startingPos = pos
        var lastVelocity = velocity

        // Iterates along our direction
        var speculative = VectorIterator(velocity, 0.01).iterator()
        var totalSteps = 0

        // Check each block along the path
        val checked: MutableList<BlockPos> = mutableListOf()
        // While theres more steps and we haven't done 200 steps yet
        while (speculative.hasNext() && totalSteps < 200) {
            totalSteps++
            val next = startingPos.add(speculative.next())
            val toBlockPos = BlockPos(next)

            pos = next
            if (Random.nextBoolean()) {
                display()
            }

            // If we haven't check this block yet
            if (!checked.contains(toBlockPos)) {
                // Save that we checked it
                checked.add(toBlockPos)

                // Check the block
                val blockState = context.world.getBlockState(toBlockPos)
                // We are in the ground
                if (!blockState.isAir && (blockState.block as CollidableAccessorMixin).collidable) {

                    if (lastChecked != null) {
                        // Save the direction we hit from and fire the hitGround event
                        context.dataStorage["hit_ground_direction"] = MathUtil.blockPosChangeToDirection(lastChecked!!, toBlockPos)
                        events.hitGround.fire(context)

                        // Something changed our velocity (i.e. bounce component)
                        if (velocity != lastVelocity) {
                            // Update our system and starting moving that way
                            lastVelocity = velocity
                            speculative = VectorIterator(velocity, 0.01).iterator()
                        }
                    }
                } else {
                    // Save this as an air block
                    context.dataStorage["last_air_block"] = lastChecked ?: toBlockPos
                }
                lastChecked = toBlockPos
            }
        }

        velocity = velocity.subtract(0.0, gravity, 0.0)

        if (pos.y < 0) {
            VeneMain.SPELLS_TO_BE_REMOVED.add(this)
        }

        lifetime--
        if (lifetime <= 0 || queue.isEmpty()) {
            VeneMain.SPELLS_TO_BE_REMOVED.add(this)
        }
    }
}