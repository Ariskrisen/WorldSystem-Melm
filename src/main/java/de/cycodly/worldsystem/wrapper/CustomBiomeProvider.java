package de.cycodly.worldsystem.wrapper;

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CustomBiomeProvider extends BiomeProvider {

    private final List<BiomeWeight> allowedBiomes;
    private final Map<Long, NoiseGenerators> worldGenerators = new HashMap<>();
    private static final Map<Biome, BiomeParams> BIOME_PARAMS = new HashMap<>();

    static {
        // Oceans
        register(Biome.OCEAN, -0.2, 0.5);
        register(Biome.DEEP_OCEAN, -0.2, 0.5);
        register(Biome.WARM_OCEAN, 0.5, 0.5);
        register(Biome.LUKEWARM_OCEAN, 0.2, 0.5);
        register(Biome.COLD_OCEAN, -0.5, 0.5);
        register(Biome.FROZEN_OCEAN, -0.8, 0.5);
        
        // Rivers
        register(Biome.RIVER, 0.0, 0.5);
        register(Biome.FROZEN_RIVER, -0.8, 0.5);
        
        // Plains
        register(Biome.PLAINS, 0.0, 0.0);
        register(Biome.SUNFLOWER_PLAINS, 0.0, 0.0);
        register(Biome.MEADOW, 0.0, 0.1);
        
        // Forests
        register(Biome.FOREST, 0.1, 0.2);
        register(Biome.FLOWER_FOREST, 0.1, 0.3);
        register(Biome.BIRCH_FOREST, 0.1, 0.2);
        register(Biome.OLD_GROWTH_BIRCH_FOREST, 0.1, 0.3);
        register(Biome.DARK_FOREST, 0.1, 0.4);
        register(Biome.CHERRY_GROVE, 0.1, 0.15);
        
        // Hot/Dry
        register(Biome.DESERT, 0.8, -0.8);
        register(Biome.BADLANDS, 0.9, -0.9);
        register(Biome.WOODED_BADLANDS, 0.8, -0.7);
        register(Biome.ERODED_BADLANDS, 0.9, -0.9);
        register(Biome.SAVANNA, 0.7, -0.5);
        register(Biome.SAVANNA_PLATEAU, 0.7, -0.5);
        register(Biome.WINDSWEPT_SAVANNA, 0.6, -0.6);
        register(Biome.WINDSWEPT_HILLS, 0.5, -0.5);
        
        // Cold/Snowy
        register(Biome.SNOWY_PLAINS, -0.8, 0.1);
        register(Biome.ICE_SPIKES, -0.9, 0.1);
        register(Biome.SNOWY_TAIGA, -0.7, 0.2);
        register(Biome.SNOWY_SLOPES, -0.8, 0.3);
        register(Biome.FROZEN_PEAKS, -0.9, 0.0);
        register(Biome.JAGGED_PEAKS, -0.9, 0.0);
        
        // Jungle
        register(Biome.JUNGLE, 0.8, 0.8);
        register(Biome.SPARSE_JUNGLE, 0.7, 0.7);
        register(Biome.BAMBOO_JUNGLE, 0.8, 0.9);
        
        // Swamps
        register(Biome.SWAMP, 0.2, 0.8);
        register(Biome.MANGROVE_SWAMP, 0.3, 0.9);
        
        // Taiga
        register(Biome.TAIGA, -0.2, 0.2);
        register(Biome.OLD_GROWTH_PINE_TAIGA, -0.2, 0.3);
        register(Biome.OLD_GROWTH_SPRUCE_TAIGA, -0.3, 0.4);
    }

    private static void register(Biome biome, double temp, double humidity) {
        BIOME_PARAMS.put(biome, new BiomeParams(temp, humidity));
    }

    public CustomBiomeProvider(ConfigurationSection section) {
        this.allowedBiomes = new ArrayList<>();
        if (section != null && section.contains("biomes")) {
            // Try as a list first
            if (section.isList("biomes")) {
                List<String> biomeNames = section.getStringList("biomes");
                for (String biomeName : biomeNames) {
                    try {
                        allowedBiomes.add(new BiomeWeight(Biome.valueOf(biomeName.toUpperCase().replace(" ", "_")), 1.0));
                    } catch (Exception ignored) {}
                }
            } else {
                // Treat as map (key-value pairs like DESERT: 10)
                ConfigurationSection bSection = section.getConfigurationSection("biomes");
                if (bSection == null) {
                    // Try getting keys from the raw map
                    Object biomesObj = section.get("biomes");
                    if (biomesObj instanceof Map) {
                        Map<?, ?> biomesMap = (Map<?, ?>) biomesObj;
                        for (Map.Entry<?, ?> entry : biomesMap.entrySet()) {
                            try {
                                String biomeName = entry.getKey().toString();
                                double weight = 1.0;
                                if (entry.getValue() instanceof Number) {
                                    weight = ((Number) entry.getValue()).doubleValue();
                                }
                                allowedBiomes.add(new BiomeWeight(Biome.valueOf(biomeName.toUpperCase().replace(" ", "_")), weight));
                            } catch (Exception ignored) {}
                        }
                    }
                } else {
                    for (String key : bSection.getKeys(false)) {
                        try {
                            double weight = bSection.getDouble(key, 1.0);
                            allowedBiomes.add(new BiomeWeight(Biome.valueOf(key.toUpperCase().replace(" ", "_")), weight));
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    public CustomBiomeProvider() {
        this.allowedBiomes = new ArrayList<>();
    }

    public boolean hasCustomBiomes() {
        return !allowedBiomes.isEmpty();
    }

    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        if (allowedBiomes.isEmpty()) {
            return Biome.PLAINS;
        }

        NoiseGenerators generators = worldGenerators.computeIfAbsent(worldInfo.getSeed(), NoiseGenerators::new);

        double temp = generators.tempObj.noise(x, z, 0.5, 0.5, true);
        double humidity = generators.humidityObj.noise(x, z, 0.5, 0.5, true);

        Biome closest = allowedBiomes.get(0).biome;
        double minDistance = Double.MAX_VALUE;

        for (BiomeWeight bw : allowedBiomes) {
            BiomeParams params = BIOME_PARAMS.getOrDefault(bw.biome, new BiomeParams(0, 0));
            // Apply weight to distance: higher weight = smaller distance = larger area
            double distance = params.distanceSq(temp, humidity) / bw.weight;
            if (distance < minDistance) {
                minDistance = distance;
                closest = bw.biome;
            }
        }

        return closest;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        if (allowedBiomes.isEmpty()) return List.of(Biome.PLAINS);
        List<Biome> list = new ArrayList<>();
        for (BiomeWeight bw : allowedBiomes) list.add(bw.biome);
        return list;
    }

    private static class BiomeWeight {
        final Biome biome;
        final double weight;

        BiomeWeight(Biome biome, double weight) {
            this.biome = biome;
            this.weight = Math.max(0.01, weight);
        }
    }

    private static class BiomeParams {
        final double temperature;
        final double humidity;

        BiomeParams(double temperature, double humidity) {
            this.temperature = temperature;
            this.humidity = humidity;
        }

        double distanceSq(double t, double h) {
            double dt = temperature - t;
            double dh = humidity - h;
            return dt * dt + dh * dh;
        }
    }

    private static class NoiseGenerators {
        final SimplexOctaveGenerator tempObj;
        final SimplexOctaveGenerator humidityObj;

        NoiseGenerators(long seed) {
            Random random = new Random(seed);
            this.tempObj = new SimplexOctaveGenerator(random, 8);
            this.humidityObj = new SimplexOctaveGenerator(random, 8);
            this.tempObj.setScale(0.00125);
            this.humidityObj.setScale(0.00125);
        }
    }
}
