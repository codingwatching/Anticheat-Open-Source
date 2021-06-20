package me.rhys.bedrock.checks.misc.badpackets;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInFlyingPacket;
import org.bukkit.Bukkit;

@CheckInformation(checkName = "BadPackets", checkType = "C", punishmentVL = 2)
public class BadPacketsC extends Check {

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                User user = event.getUser();

                double deltaXZ = user.getMovementProcessor().getDeltaXZ();

                double maxSpeed = user.getMovementProcessor().getServerPositionSpeed();

                if (user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)) {
                    if (deltaXZ > maxSpeed && deltaXZ > 1.0) {
                        flag(user, "Invalid Teleport "+deltaXZ + " "+maxSpeed);
                    }
                }
                break;
            }
        }
    }
}