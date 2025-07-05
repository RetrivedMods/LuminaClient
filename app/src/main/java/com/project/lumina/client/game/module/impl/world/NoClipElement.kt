package com.project.lumina.client.game.module.impl.world

import com.project.lumina.client.R
import com.project.lumina.client.constructors.CheatCategory
import com.project.lumina.client.constructors.Element
import com.project.lumina.client.game.InterceptablePacket
import org.cloudburstmc.protocol.bedrock.data.Ability
import org.cloudburstmc.protocol.bedrock.data.PlayerPermission
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermission
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.RequestAbilityPacket
import org.cloudburstmc.protocol.bedrock.packet.UpdateAbilitiesPacket

class NoClipElement(iconResId: Int = R.drawable.ic_circle_double_black_24dp) : Element(
    name = "NoClip",
    category = CheatCategory.World,
    iconResId,
    displayNameResId = R.string.module_no_clip_display_name
) {


    private var noClipEnabled = false

    private fun createAbilitiesPacket(enable: Boolean): UpdateAbilitiesPacket {
        return UpdateAbilitiesPacket().apply {
            playerPermission = PlayerPermission.OPERATOR
            commandPermission = CommandPermission.ANY
            uniqueEntityId = session.localPlayer.uniqueEntityId
        }
    }

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        val packet = interceptablePacket.packet

        if (packet is RequestAbilityPacket && packet.ability == Ability.NO_CLIP) {
            interceptablePacket.intercept()
            return
        }

        if (packet is UpdateAbilitiesPacket) {
            interceptablePacket.intercept()
            return
        }

        if (packet is PlayerAuthInputPacket) {
            if (isEnabled && !noClipEnabled) {
                session.clientBound(createAbilitiesPacket(true))
                noClipEnabled = true
            } else if (!isEnabled && noClipEnabled) {
                session.clientBound(createAbilitiesPacket(false))
                noClipEnabled = false
            }
        }
    }
}
