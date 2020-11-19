/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.event

class EventInstance {
    val moveTick = SpellEvent()
    val physicsTick = SpellEvent()
    val hitGround = SpellEvent()
    val inGround = SpellEvent()
}