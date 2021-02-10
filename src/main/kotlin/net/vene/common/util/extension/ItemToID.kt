/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.util.extension

import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

fun Item.formattedID() = '"' + Registry.ITEM.getId(this).toString() + '"'

fun Item.cleanID() = Registry.ITEM.getId(this).toString()

fun Item.id() = Registry.ITEM.getId(this)
