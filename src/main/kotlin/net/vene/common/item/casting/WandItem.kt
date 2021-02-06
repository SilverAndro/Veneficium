/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.item.casting

import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.vene.ConfigInstance
import net.vene.VeneMain
import net.vene.cca_component.WandSpellsComponent
import net.vene.common.util.appendTooltipFromStack
import net.vene.common.util.displayFromUnderscored
import net.vene.magic.SpellExecutor
import net.vene.magic.handling.SpellQueue
import net.vene.common.util.math.MathUtil

class WandItem(settings: Settings) : Item(settings), SpellProvider {
    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if (user != null && world is ServerWorld) {
            // Generate the relevant info
            val queue = SpellQueue()
            val stack = user.getStackInHand(hand)
            val spells = WandSpellsComponent.getSpellsFrom(stack)

            // Add components to queue
            for (component in spells) {
                if (component != null) {
                    queue.addToQueue(component)
                }
            }

            // Get what direction it should fire in
            val velocity = MathUtil.facingToVector(user.yaw, user.pitch).multiply(2.0, 2.0, 2.0)
            // Create an executor
            val executor = SpellExecutor(user, world, Vec3d(user.x, user.eyeY, user.z), velocity, queue.copy())
            // Cooldown
            user.itemCooldownManager[this] = ConfigInstance.wandCastDelay
            // Add it to active executors
            VeneMain.ACTIVE_SPELLS.add(executor)

            // Damage item
            stack.damage(1, user) { e: LivingEntity ->
                WandSpellsComponent.getComponentItems(stack).forEach {
                    ItemScatterer.spawn(world, e.pos.x, e.pos.y, e.pos.z, ItemStack(it))
                }
                e.sendToolBreakStatus(hand)
            }
        }
        return TypedActionResult.consume(user?.getStackInHand(hand))
    }

    override fun getRarity(stack: ItemStack?): Rarity {
        return Rarity.EPIC
    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        if (tooltip != null) {
            appendTooltipFromStack(stack, tooltip, context ?: TooltipContext.Default.NORMAL)
        }
    }

    override fun getMaxSpells(): Int {
        return 9
    }
}
