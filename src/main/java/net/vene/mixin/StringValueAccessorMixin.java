/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.mixin;

import net.minecraft.nbt.StringTag;
import net.vene.access.StringValueAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StringTag.class)
public class StringValueAccessorMixin implements StringValueAccessor {
    @Final
    @Shadow
    private String value;
    
    
    @Override
    public String getValue() {
        return value;
    }
}
