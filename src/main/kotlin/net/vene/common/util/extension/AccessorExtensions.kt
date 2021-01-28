/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.util.extension

import net.minecraft.block.AbstractBlock
import net.minecraft.nbt.StringTag
import net.vene.mixin.CollidableAccessorMixin
import net.vene.mixin.StringValueAccessorMixin

fun StringTag.getRawValue(): String {
    return (this as StringValueAccessorMixin).value
}

fun AbstractBlock.isCollidable(): Boolean {
    return (this as CollidableAccessorMixin).collidable
}
