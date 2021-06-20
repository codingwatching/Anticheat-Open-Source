package me.rhys.bedrock.checks.misc.scaffold;

import me.rhys.bedrock.base.check.api.Check;
import me.rhys.bedrock.base.check.api.CheckInformation;
import me.rhys.bedrock.base.event.PacketEvent;
import me.rhys.bedrock.base.user.User;
import me.rhys.bedrock.tinyprotocol.api.Packet;
import me.rhys.bedrock.tinyprotocol.packet.in.WrappedInBlockPlacePacket;
import me.rhys.bedrock.util.MathUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@CheckInformation(checkName = "Scaffold", checkType = "F", lagBack = false, punishmentVL = 10)
public class ScaffoldF extends Check {

    private List<Float> placeList = new ArrayList<>();
    private double lastSTD, threshold;

    @Override
    public void onPacket(PacketEvent event) {
        switch (event.getType()) {
            case Packet.Client.BLOCK_PLACE: {
                User user = event.getUser();

                WrappedInBlockPlacePacket blockPlace =
                        new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());

                float vecY = blockPlace.getVecY();

                int faceInt = blockPlace.getFace().b();

                double yaw = Math.abs(user.getCurrentLocation().getYaw() - user.getLastLocation().getYaw());

                if (yaw > 0) {
                    if (faceInt >= 0 && faceInt <= 2) {
                        placeList.add(vecY);

                        if (placeList.size() == 5) {
                            double std = MathUtil.getStandardDeviation(placeList);

                            if (std < 0.05 || std == lastSTD) {
                                if (++threshold > 3) {
                                    flag(user, "HitVec Consistency");
                                }
                            } else {
                                threshold -= Math.min(threshold, 0.5);
                            }

                            lastSTD = std;
                            placeList.clear();
                        }
                    }
                }
                break;
            }
        }
    }
}