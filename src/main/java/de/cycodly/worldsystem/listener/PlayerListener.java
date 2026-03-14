package de.cycodly.worldsystem.listener;

import de.cycodly.worldsystem.config.DependenceConfig;
import de.cycodly.worldsystem.config.PluginConfig;
import de.cycodly.worldsystem.config.WorldConfig;
import de.cycodly.worldsystem.util.PlayerPositions;
import de.cycodly.worldsystem.util.PlayerWrapper;
import de.cycodly.worldsystem.wrapper.SystemWorld;
import de.cycodly.worldsystem.wrapper.WorldPlayer;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    private final HashMap<UUID, World> deathLocations = new HashMap<>();

    // #17
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        PlayerWrapper.updateDatabase(e.getPlayer());
        if (PluginConfig.spawnTeleportation()) {
            Player p = e.getPlayer();
            DependenceConfig dc = new DependenceConfig(p);
            if (dc.hasWorld()) {
                SystemWorld sw = SystemWorld.getSystemWorld(dc.getWorldname());
                if (sw != null && !sw.isLoaded()) {
                    Location spawnLoc = PluginConfig.getSpawn(e.getPlayer());
                    if (spawnLoc != null && spawnLoc.getWorld() != null) {
                        e.getPlayer().teleport(spawnLoc);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        World w = p.getWorld();
        WorldPlayer player = new WorldPlayer(p);
        // Save last location for #23
        if (player.isOnSystemWorld()) {
            WorldConfig config = WorldConfig.getWorldConfig(player.getWorldname());
            PlayerPositions.instance.saveWorldsPlayerLocation(p, config);
        }
        SystemWorld.tryUnloadLater(w);
    }

    @EventHandler
    public void onDie(PlayerDeathEvent e) {
        Player p = e.getEntity();
        WorldPlayer wp = new WorldPlayer(p, p.getWorld().getName());
        if (wp.isOnSystemWorld()) {
            deathLocations.put(p.getUniqueId(), p.getLocation().getWorld());
        } else {
            p.setGameMode(PluginConfig.getSpawnGamemode());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (deathLocations.containsKey(p.getUniqueId())) {
            World world = deathLocations.remove(p.getUniqueId());

            WorldConfig config = WorldConfig.getWorldConfig(world.getName());

            if (config.getHome() != null) {
                e.setRespawnLocation(config.getHome());
            } else {
                if (PluginConfig.useWorldSpawn()) {
                    e.setRespawnLocation(PluginConfig.getWorldSpawn(world));
                } else {
                    e.setRespawnLocation(world.getSpawnLocation());
                }
            }
        }
    }

}