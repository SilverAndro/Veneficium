/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.item.casting

import net.minecraft.item.Item

class InfusedStick(settings: Settings) : Item(settings), SpellProvider {
    override fun getMaxSpells(): Int {
        return 5
    }
}
