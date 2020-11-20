/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.item

import net.minecraft.item.Item
import net.vene.magic.spell_components.MagicEffect

// Only for differentiation and storing effects with items
class ComponentItem(settings: Settings, val effect: MagicEffect) : Item(settings)