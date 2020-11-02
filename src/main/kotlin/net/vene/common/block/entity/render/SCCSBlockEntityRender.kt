/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.vene.common.block.entity.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import net.minecraft.item.Items
import net.minecraft.util.math.Quaternion
import net.vene.VeneMain
import net.vene.common.block.entity.SCCSBlockEntity
import java.util.*
import kotlin.math.sin

class SCCSBlockEntityRender(dispatcher: BlockEntityRenderDispatcher) : BlockEntityRenderer<SCCSBlockEntity>(dispatcher) {
    override fun render(entity: SCCSBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        // Code stolen from beacon block entity render
        if (entity.isWorking) {
            val l = entity.world!!.time
            val list = entity.beamSegments
            var k = 0
            for (m in list.indices) {
                val beamSegment = list[m]
                render(matrices, vertexConsumers, tickDelta, l, k, if (m == list.size - 1) 1024 else beamSegment.height, beamSegment.color)
                k += beamSegment.height
            }
        }

        // Render the held item
        matrices.push()
        if (entity.heldItemStack.item != Items.AIR) {
            val time = entity.world?.time ?: 0

            val rotation = (time % 360).toFloat() * 5 + tickDelta

            matrices.translate(0.5, 1.15, 0.5)
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation))
            MinecraftClient.getInstance().itemRenderer.renderItem(entity.heldItemStack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers)
        }
        matrices.pop()
    }

    private fun render(matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider, f: Float, l: Long, i: Int, j: Int, fs: FloatArray) {
        BeaconBlockEntityRenderer.renderLightBeam(matrixStack, vertexConsumerProvider, BeaconBlockEntityRenderer.BEAM_TEXTURE, f, 1.0f, l, i, j, fs, 0.2f, 0.25f)
    }

    override fun rendersOutsideBoundingBox(blockEntity: SCCSBlockEntity): Boolean {
        // Beam disappears otherwise
        return true
    }
}