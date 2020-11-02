/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene

import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer
import net.minecraft.util.Identifier
import net.vene.VeneMain.Companion.MOD_ID
import net.vene.cca_component.WandSpellsComponent

class VeneCCAComponentRegistration : ItemComponentInitializer {
    override fun registerItemComponentFactories(registry: ItemComponentFactoryRegistry) {
        registry.registerFor(Identifier(MOD_ID, "wand"), VeneMain.WAND_SPELLS_COMPONENT) {_ -> WandSpellsComponent()}
    }
}