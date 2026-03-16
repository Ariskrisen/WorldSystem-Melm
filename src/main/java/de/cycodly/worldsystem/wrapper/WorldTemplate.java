package de.cycodly.worldsystem.wrapper;

import de.cycodly.worldsystem.guicreate.OrcItem;
import de.cycodly.worldsystem.config.GuiConfig;
import lombok.Getter;

/**
 * @author Butzlabben
 * @since 15.12.2018
 */
@Getter
public class WorldTemplate {

    public final String name;
    public final String permission;
    public final OrcItem icon;
    public final int slot;
    public final int cost;
    public final GeneratorSettings generatorSettings;
    public final String spawnBiome;

    public WorldTemplate(String name, String permission, int cost, GeneratorSettings generatorSettings, String spawnBiome) {
        this.name = name;
        this.permission = permission;
        this.cost = cost;
        this.generatorSettings = generatorSettings;
        this.spawnBiome = spawnBiome;

        this.icon = GuiConfig.getItem("worldchoose." + name);
        this.slot = GuiConfig.getSlot("worldchoose." + name);

        icon.setOnClick((p, inv, item) -> {
            p.closeInventory();
            p.chat("/ws get " + name);
        });
    }

    public int getSlot() {
        return slot;
    }

    public int getCost() {
        return cost;
    }

    public OrcItem getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return "plugins/WorldSystem/worldsources/" + name;
    }

    public String getPermission() {
        return permission;
    }

    public String getSpawnBiome() {
        return spawnBiome;
    }
}
