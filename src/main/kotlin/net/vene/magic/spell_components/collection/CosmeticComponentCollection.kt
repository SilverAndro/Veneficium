/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.magic.spell_components.collection

import net.minecraft.particle.ParticleTypes
import net.vene.magic.spell_components.ComponentFactories

@Suppress("unused")
object CosmeticComponentCollection {
    val NO_TRAIL = ComponentFactories.trailParticleBuilder("no", null)
    val NORMAL_TRAIL = ComponentFactories.trailParticleBuilder("normal", ParticleTypes.ENCHANTED_HIT)
    val FIRE_TRAIL = ComponentFactories.trailParticleBuilder("fire", ParticleTypes.FLAME)
    val SOUL_FIRE_TRAIL = ComponentFactories.trailParticleBuilder("soul_fire", ParticleTypes.SOUL_FIRE_FLAME)
    val MAGIC_TRAIL = ComponentFactories.trailParticleBuilder("magic", ParticleTypes.WITCH)
    val HEART_TRAIL = ComponentFactories.trailParticleBuilder("heart", ParticleTypes.HEART)
}
