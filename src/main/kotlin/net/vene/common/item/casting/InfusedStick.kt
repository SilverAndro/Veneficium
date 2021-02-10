/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.item.casting

import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.vene.ConfigInstance
import net.vene.VeneMain
import net.vene.cca_component.WandSpellsComponent
import net.vene.common.util.appendTooltipFromStack
import net.vene.common.util.math.MathUtil
import net.vene.magic.SpellExecutor
import net.vene.magic.handling.SpellQueue

class InfusedStick(settings: Settings) : Item(settings), SpellProvider {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (user is ServerPlayerEntity && world is ServerWorld) {
            val stack = user.getStackInHand(hand)
            val velocity = MathUtil.facingToVector(user.yaw, user.pitch).multiply(2.0, 2.0, 2.0)
            fireSpells(user.pos, velocity, world, stack, user)
        }
        return TypedActionResult.consume(user.getStackInHand(hand))
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        if (tooltip != null) {
            appendTooltipFromStack(stack, tooltip, context ?: TooltipContext.Default.NORMAL)
        }
    }

    override fun getMaxSpells(): Int {
        return 3
    }

    override fun fireSpells(pos: Vec3d, facing: Vec3d, world: ServerWorld, stack: ItemStack, user: ServerPlayerEntity?) {
        // Generate the relevant info
        val queue = SpellQueue()
        val spells = WandSpellsComponent.getSpellsFrom(stack)

        // Add components to queue
        for (component in spells) {
            if (component != null) {
                queue.addToQueue(component)
            }
        }

        // Create an executor
        val executor = SpellExecutor(
            user,
            user?.pos ?: pos,
            world,
            Vec3d(user?.x ?: pos.x, user?.eyeY ?: pos.y, user?.z ?: pos.z),
            facing,
            queue.copy()
        )
        // Cooldown
        user?.itemCooldownManager?.set(this, ConfigInstance.infusedStickCastDelay)
        // Add it to active executors
        VeneMain.ACTIVE_SPELLS.add(executor)

        // Damage item
        if (stack.damage(1, world.random, user)) {
            if (user != null) {
                WandSpellsComponent.getComponentItems(stack).forEach {
                    ItemScatterer.spawn(world, user.pos.x, user.pos.y, user.pos.z, ItemStack(it))
                }
                user.sendToolBreakStatus(Hand.MAIN_HAND)
            } else {
                WandSpellsComponent.getComponentItems(stack).forEach {
                    ItemScatterer.spawn(world, pos.x, pos.y, pos.z, ItemStack(it))
                }
            }
            stack.count = 0
        }
    }
}
