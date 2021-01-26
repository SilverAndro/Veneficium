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
