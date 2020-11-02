/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.mixin;

import net.minecraft.block.AbstractBlock;
import net.vene.access.CollidableAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractBlock.class)
public class CollidableAccessorMixin implements CollidableAccessor {
    @Final
    @Shadow
    protected
    boolean collidable;
    
    @Override
    public boolean getCollidable() {
        return collidable;
    }
}
