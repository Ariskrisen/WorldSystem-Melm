package de.cycodly.worldsystem.wrapper;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class GeneratorSettings {
    private final long seed;
    private final World.Environment environment;
    private final WorldType type;
    private final String generator;
    private final CustomBiomeProvider biomeProvider;

    public GeneratorSettings(long seed, World.Environment environment, WorldType type, String generator, CustomBiomeProvider biomeProvider) {
        this.seed = seed;
        this.environment = environment;
        this.type = type;
        this.generator = generator;
        this.biomeProvider = biomeProvider;
    }

    public GeneratorSettings(long seed, World.Environment environment, WorldType type, String generator) {
        this(seed, environment, type, generator, new CustomBiomeProvider());
    }

    public GeneratorSettings() {
        type = null;
        environment = null;
        seed = 0;
        generator = null;
        biomeProvider = new CustomBiomeProvider();
    }

    public static GeneratorSettings fromConfig(ConfigurationSection section) {
        if (section == null) {
            return new GeneratorSettings();
        }
        
        long seed = section.getLong("seed", 0);
        String envStr = section.getString("environment");
        String typeStr = section.getString("type");
        String generator = section.getString("plugin");
        
        World.Environment env = null;
        if (envStr != null) {
            try {
                env = World.Environment.valueOf(envStr);
            } catch (Exception ignored) {}
        }
        
        WorldType worldType = null;
        if (typeStr != null) {
            try {
                worldType = WorldType.valueOf(typeStr);
            } catch (Exception ignored) {}
        }
        
        CustomBiomeProvider biomeProvider = new CustomBiomeProvider(section);
        
        return new GeneratorSettings(seed, env, worldType, generator, biomeProvider);
    }

    public WorldCreator asWorldCreator(String name) {
        WorldCreator creator = new WorldCreator(name);

        if (type != null)
            creator.type(type);
        if (environment != null)
            creator.environment(environment);
        if (seed != 0)
            creator.seed(seed);
        if (generator != null && !generator.trim().isEmpty())
            creator.generator(generator);

        if (biomeProvider != null && biomeProvider.hasCustomBiomes()) {
            creator.biomeProvider(biomeProvider);
        }

        return creator;
    }
}
