/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.util.math

import net.minecraft.util.math.Vec3d

class VectorIterator(private val endVec: Vec3d, scale: Double) : Iterable<Vec3d> {
    private var current = Vec3d(0.0, 0.0, 0.0)
    private val step: Vec3d = endVec.normalize().multiply(scale)
    override fun iterator(): MutableIterator<Vec3d> {
        return object : MutableIterator<Vec3d> {
            override fun hasNext(): Boolean {
                return endVec.length().compareTo(current.length()) >= 0
            }

            override fun next(): Vec3d {
                current = current.add(step)
                return current
            }

            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

}