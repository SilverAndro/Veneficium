/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.util.extension

import net.fabricmc.loader.api.FabricLoader
import net.vene.VeneMain

fun devDebug(m: Any) {
    if (FabricLoader.getInstance().isDevelopmentEnvironment) {
        VeneMain.LOGGER.info(m)
    } else {
        VeneMain.LOGGER.debug(m)
    }
}