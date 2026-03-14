package de.cycodly.worldsystem.wrapper;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomBiomeProvider {

    private final List<BiomeType> biomes;
    private final double biomeSize;

    public CustomBiomeProvider(ConfigurationSection section) {
        this.biomes = new ArrayList<>();
        this.biomeSize = section.getDouble("biome_size", 4.0);

        if (section.contains("biomes")) {
            List<String> biomeNames = section.getStringList("biomes");
            for (String biomeName : biomeNames) {
                try {
                    Biome biome = Biome.valueOf(biomeName.toUpperCase().replace(" ", "_"));
                    this.biomes.add(new BiomeType(biome, biomeName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public CustomBiomeProvider(List<String> biomeNames, double biomeSize) {
        this.biomes = new ArrayList<>();
        this.biomeSize = biomeSize;
        
        for (String biomeName : biomeNames) {
            try {
                Biome biome = Biome.valueOf(biomeName.toUpperCase().replace(" ", "_"));
                this.biomes.add(new BiomeType(biome, biomeName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CustomBiomeProvider() {
        this.biomes = new ArrayList<>();
        this.biomeSize = 4.0;
    }

    public List<BiomeType> getBiomes() {
        return biomes;
    }

    public double getBiomeSize() {
        return biomeSize;
    }

    public boolean hasCustomBiomes() {
        return !biomes.isEmpty();
    }

    public void applyToWorldCreator(org.bukkit.WorldCreator creator, long seed) {
        if (!hasCustomBiomes()) {
            return;
        }
        
        NoiseGen noiseGen = new NoiseGen(seed);
        creator.generator(new BiomeGenerator(noiseGen, this));
    }

    public static class BiomeType {
        private final Biome biome;
        private final String name;

        public BiomeType(Biome biome, String name) {
            this.biome = biome;
            this.name = name;
        }

        public Biome getBiome() {
            return biome;
        }

        public String getName() {
            return name;
        }
    }

    public static class NoiseGen {
        private final SimplexNoiseGenerator noiseGen;

        public NoiseGen(long seed) {
            this.noiseGen = new SimplexNoiseGenerator(new Random(seed));
        }

        public SimplexNoiseGenerator getNoiseGen() {
            return noiseGen;
        }
    }

    public static class BiomeGenerator extends ChunkGenerator {
        private final NoiseGen generators;
        private final CustomBiomeProvider provider;

        public BiomeGenerator(NoiseGen generators, CustomBiomeProvider provider) {
            this.generators = generators;
            this.provider = provider;
        }

        @Override
        public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biomeGrid) {
            ChunkData chunk = createChunkData(world);
            
            double scale = provider.getBiomeSize();
            for (int sx = 0; sx < 16; sx++) {
                for (int sz = 0; sz < 16; sz++) {
                    double noise = generators.getNoiseGen().noise(x * 16 + sx, z * 16 + sz) * scale;
                    int biomeIndex = (int) Math.abs(noise) % provider.getBiomes().size();
                    if (biomeIndex < provider.getBiomes().size()) {
                        BiomeType bp = provider.getBiomes().get(biomeIndex);
                        if (bp != null && bp.getBiome() != null) {
                            biomeGrid.setBiome(sx, sz, bp.getBiome());
                        }
                    }
                }
            }
            return chunk;
        }

        @Override
        public boolean canSpawn(World world, int x, int z) {
            return true;
        }

        @Override
        public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, 0, 64, 0);
        }
    }
}
