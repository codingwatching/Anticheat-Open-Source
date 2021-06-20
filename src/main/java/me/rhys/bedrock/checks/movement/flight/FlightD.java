package me.rhys.bedrock.checks.movement.flight;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

@CheckInformation(checkName = "Flight", checkType = "D", description = "Checks if the player is to far up from the ground")
public class FlightD extends Check {

    private double serverGroundY;
    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        User user = event.getUser();

        switch (event.getType()) {
            case Packet.Client.FLYING:
            case Packet.Client.LOOK:
            case Packet.Client.POSITION_LOOK:
            case Packet.Client.POSITION: {

                if (user.shouldCancel()
                        || user.getActionProcessor().getServerPositionTimer().hasNotPassed(3)
                        || user.getLastTeleportTimer().hasNotPassed(20)
                        || user.getCombatProcessor().getVelocityTicks() <= 20
                        || checkConditions(user)) {
                    return;
                }

                double currentY = user.getCurrentLocation().getY();
                double deltaY = user.getMovementProcessor().getDeltaY();

                if (user.getBlockData().onGround) {
                    serverGroundY = currentY;
                }

                double change = currentY - serverGroundY;

                if (!user.getBlockData().onGround) {
                    if (change > 1.25 && deltaY > 0.0) {
                        if (++threshold > 3) {
                            flag(user, "Flying up to high? " + change);
                        }
                    } else {
                        threshold -= Math.min(threshold, 0.1);
                    }
                }
            }
        }
    }
    boolean checkConditions(User user) {
        return user.getBlockData().liquidTicks > 0
                || user.getTick() < 100
                || user.shouldCancel()
                || user.getBlockData().climbableTicks > 0
                || user.getBlockData().climbableTimer.hasNotPassed();
    }
}
