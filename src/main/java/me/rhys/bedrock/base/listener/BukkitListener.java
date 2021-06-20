package me.rhys.bedrock.base.listener;

import me.rhys.bedrock.Anticheat;
import me.rhys.bedrock.base.user.User;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BukkitListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        this.processEvent(event);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        this.processEvent(event);
    }

    void processEvent(Event event) {
        Anticheat.getInstance().getExecutorService().execute(() -> this.process(event));
    }

    void process(Event event) {
        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
            User user = Anticheat.getInstance().getUserManager().getUser(playerInteractEvent.getPlayer());

            if (user != null) {
                if (playerInteractEvent.getItem().getType() == Material.FIREWORK) {
                    user.getElytraProcessor().setFireworkBoost(2.3);
                }
            }
        }

        if (event instanceof PlayerTeleportEvent) {
            User user = Anticheat.getInstance().getUserManager().getUser(((PlayerTeleportEvent) event).getPlayer());

            if (user != null) {
                if (((PlayerTeleportEvent) event).getCause() != PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                    user.getLastTeleportTimer().reset();
                }

                if (((PlayerTeleportEvent) event).getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
                    user.getLastUnknownTeleportTimer().reset();
                }
            }
        }

        if (event instanceof EntityDamageEvent) {
            User user = Anticheat.getInstance().getUserManager().getUser((Player) ((EntityDamageEvent) event).getEntity());

            if (user != null) {
                if (((EntityDamageEvent) event).getCause() == EntityDamageEvent.DamageCause.FALL) {
                    user.getLastFallDamageTimer().reset();
                }
            }
        }
    }
}
