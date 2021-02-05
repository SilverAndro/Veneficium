/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.item.casting

import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.CrossbowItem
import net.minecraft.item.ItemStack
import net.minecraft.item.RangedWeaponItem
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.vene.ConfigInstance
import net.vene.VeneMain
import net.vene.cca_component.WandSpellsComponent
import net.vene.common.util.StringUtil
import net.vene.common.util.math.MathUtil
import net.vene.magic.SpellExecutor
import net.vene.magic.handling.SpellQueue
import net.vene.magic.spell_components.collection.MoveComponentCollection
import net.vene.magic.spell_components.collection.ResultComponentCollection
import java.util.function.Consumer
import java.util.function.Predicate

class MagicCrossbow(settings: Settings) : CrossbowItem(settings), SpellProvider {
    override fun getHeldProjectiles(): Predicate<ItemStack> {
        return Predicate { true }
    }

    override fun getProjectiles(): Predicate<ItemStack> {
        return Predicate { true }
    }

    override fun isUsedOnRelease(stack: ItemStack?): Boolean {
        return true
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val itemStack = user.getStackInHand(hand)
        return if (isCharged(itemStack)) {
            setCharged(itemStack, false)
            fireSpell(world, user, hand)
        } else {
            if (!isCharged(itemStack)) {
                setCharged(itemStack, false)
                user.setCurrentHand(hand)
            }
            TypedActionResult.consume(itemStack)
        }
    }

    override fun onStoppedUsing(stack: ItemStack, world: World, user: LivingEntity, remainingUseTicks: Int) {
        val i = getMaxUseTime(stack) - remainingUseTicks
        val f = getPullProgress(i, stack)
        if (f >= 1.0f && !isCharged(stack)) {
            setCharged(stack, true)
            val soundCategory = if (user is PlayerEntity) SoundCategory.PLAYERS else SoundCategory.HOSTILE
            world.playSound(
                null,
                user.x,
                user.y,
                user.z,
                SoundEvents.ITEM_CROSSBOW_LOADING_END,
                soundCategory,
                1.0f,
                1.0f / (RANDOM.nextFloat() * 0.5f + 1.0f) + 0.2f
            )
        }
    }

    private fun fireSpell(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world is ServerWorld) {
            // Generate the relevant info
            val queue = SpellQueue()
            val stack = user.getStackInHand(hand)
            val spells = WandSpellsComponent.getSpellsFrom(stack)

            queue.addToQueue(MoveComponentCollection.LOW_GRAV)
            queue.addToQueue(MoveComponentCollection.LOW_GRAV)

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
            user.itemCooldownManager[this] = ConfigInstance.crossbowCastDelay
            // Add it to active executors
            VeneMain.ACTIVE_SPELLS.add(executor)

            // Damage item
            stack.damage(1, user) { e: LivingEntity -> e.sendToolBreakStatus(hand) }
        }
        return TypedActionResult.consume(user.getStackInHand(hand))
    }

    override fun appendTooltip(stack: ItemStack?, world: World?, tooltip: MutableList<Text>?, context: TooltipContext?) {
        // For each component get its display name and add it
        for (component in stack?.let { WandSpellsComponent.getSpellsFrom(it) } ?: mutableListOf()) {
            if (component != null) {
                tooltip?.add(TranslatableText(StringUtil.displayFromUnderscored(component.name)))
            }
        }
    }

    override fun getRarity(stack: ItemStack?): Rarity {
        return Rarity.EPIC
    }

    private fun getPullProgress(useTicks: Int, stack: ItemStack): Float {
        var f = useTicks.toFloat() / getPullTime(stack).toFloat()
        if (f > 1.0f) {
            f = 1.0f
        }
        return f
    }
}
