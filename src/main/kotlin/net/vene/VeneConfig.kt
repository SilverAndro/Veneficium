/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene

import mc.microconfig.Comment
import mc.microconfig.ConfigData
import mc.microconfig.MicroConfig

class VeneConfig : ConfigData {
    @JvmField
    @Comment("Debugging - This will enable output of development debug messages")
    var enableDevMessages = false

    @JvmField
    @Comment("Debugging - This will generate a massive amount of debug output that traces the actions taken by every spell every tick, requires enableDevMessages")
    var spellQueueTraceback = false

    @JvmField
    @Comment("Debugging - This will dump all runtime generated assets (including loot tables, recipes, and lang files) to rrp.debug/vene;rrp")
    var dumpRuntimeGeneratedAssets = false

    @JvmField
    @Comment("Allows explosions to create materials (Can be powerful tool for griefing)\nNote that this doesn't disable \"Create Material\"")
    var explosionsCreateMaterials = true

    @JvmField
    @Comment("Delay until wands can be used again after crafting (in ticks)")
    var wandCastDelay = 40

    @JvmField
    @Comment("Delay until magic crossbows can be used again after crafting (in ticks)")
    var crossbowCastDelay = 0
}

val ConfigInstance: VeneConfig by lazy { MicroConfig.getOrCreate("veneficium", VeneConfig()) }
