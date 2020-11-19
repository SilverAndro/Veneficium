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