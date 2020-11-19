/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.magic.handling

import net.vene.VeneConfig
import net.vene.VeneMain
import net.vene.common.magic.SpellContext
import net.vene.common.magic.spell_components.ComponentType
import net.vene.common.magic.spell_components.MagicEffect
import net.vene.common.magic.spell_components.MaterialComponent
import net.vene.common.util.extension.devDebug

class SpellQueue {
    private val componentList: MutableList<MagicEffect> = mutableListOf()
    private val modifiers: MutableList<MaterialComponent> = mutableListOf()

    private var index = 0

    fun run(context: SpellContext) {
        if (VeneConfig.SpellQueueTraceback) {
            devDebug("------ Queue Run ------")
        }

        var tmpIndex = 0
        while (tmpIndex < componentList.size) {

            val magicEffect = componentList[tmpIndex]
            if (VeneConfig.SpellQueueTraceback) {
                devDebug("Executing component $magicEffect")
            }

            val operation: HandlerOperation
            try {
                operation = magicEffect.exec(context, modifiers, this)
            } catch (thrown: Throwable) {
                VeneMain.LOGGER.error("EXECUTE COMPONENT FAILED!")
                VeneMain.LOGGER.error("Attempting to execute $magicEffect yielded a throwable")
                VeneMain.LOGGER.error("State After Error: $this")
                VeneMain.LOGGER.error("Index: $index")
                VeneMain.LOGGER.error("Throwable: ${thrown.message}")
                VeneMain.LOGGER.error("Ditching component in attempt to recover from error")
                componentList.remove(magicEffect)
                continue
            }
            if (VeneConfig.SpellQueueTraceback) {
                devDebug("Returned Operation is $operation")
            }

            val result = handleOp(operation, magicEffect)

            if (result.stop) {
                break
            }
            if (result.increment) {
                tmpIndex++
            }

        }
        index = 0
    }

    // Not private in case I want to mess with the queue from a component safely
    @Suppress("MemberVisibilityCanBePrivate")
    fun handleOp(operation: HandlerOperation, magicEffect: MagicEffect): OpResult {
        when (operation) {
            HandlerOperation.REMOVE_CONTINUE -> {
                componentList.remove(magicEffect)
                return OpResult(increment = false, stop = false)
            }
            HandlerOperation.REMOVE_STOP -> {
                componentList.remove(magicEffect)
                return OpResult(increment = false, stop = true)
            }
            HandlerOperation.STAY_CONTINUE -> {
                index++
            }
            HandlerOperation.STAY_STOP -> {
                return OpResult(increment = false, stop = true)
            }
            HandlerOperation.MATERIAL_MOVE -> {
                return if (magicEffect.type == ComponentType.MATERIAL) {
                    modifiers.add(magicEffect as MaterialComponent)
                    componentList.remove(magicEffect)
                    OpResult(increment = false, stop = false)
                } else {
                    // remove continue
                    componentList.remove(magicEffect)
                    OpResult(increment = false, stop = false)
                }
            }
        }
        throw EnumConstantNotPresentException(operation.javaClass, operation.name)
    }

    fun isEmpty(): Boolean {
        return componentList.isEmpty()
    }

    fun addToQueue(effect: MagicEffect) {
        componentList.add(effect)
    }

    fun copy(): SpellQueue {
        val copy = SpellQueue()
        copy.componentList.addAll(componentList)
        copy.modifiers.addAll(modifiers)
        return copy
    }

    override fun toString(): String {
        return "SpellQueue[Components: $componentList, Materials: $modifiers]"
    }

    data class OpResult(val increment: Boolean, val stop: Boolean)
}

enum class HandlerOperation {
    REMOVE_CONTINUE,
    REMOVE_STOP,
    STAY_CONTINUE,
    STAY_STOP,
    MATERIAL_MOVE,
}