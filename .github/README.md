# WorldSystem

A Minecraft plugin that allows players to create their own worlds.

## Features

- **Custom World Templates** - Players can choose from different world templates with custom biomes
- **Personal Worlds** - Each player can have their own private world
- **World Management** - Full control over world settings (TNT, Fire, GameMode, etc.)
- **GUI Interface** - Easy-to-use GUI for world selection and management
- **MySQL/SQLite Support** - Store player data in MySQL or SQLite database
- **Economy Integration** - Optional cost for creating worlds (requires Vault)


## Documentation

| Language | Link |
|----------|------|
| 🇷🇺 Русский | [https://ariskrisen.github.io/Docs/docs/WorldSystem/intro/](https://ariskrisen.github.io/Docs/docs/WorldSystem/intro/) |
| 🇬🇧 English | [https://ariskrisen.github.io/Docs/en/docs/WorldSystem/intro](https://ariskrisen.github.io/Docs/en/docs/WorldSystem/intro) |

## Commands

| Command | Description |
|---------|-------------|
| `/ws get [template]` | Create a new world |
| `/ws home` | Teleport to your world |
| `/ws leave` | Leave your world |
| `/ws tp <world>` | Teleport to another world |
| `/ws gui` | Open world settings GUI |
| `/ws info` | Show world information |
| `/ws addmember <player>` | Add a player to your world |
| `/ws delmember <player>` | Remove a player from your world |
| `/ws delete` | Delete your world (requires permission) |

### Admin Commands

| Command | Description |
|---------|-------------|
| `/ws get <player> <template>` | Create a world for another player |
| `/ws togglebuild` | Toggle build permissions for a player |
| `/ws togglegm` | Toggle gamemode permissions |
| `/ws toggletp` | Toggle teleport permissions |
| `/ws tnt` | Toggle TNT explosions |
| `/ws fire` | Toggle fire spread |
| `/ws reset` | Reset world |

## World Templates

The plugin includes custom biome templates:

### template_default
Standard Minecraft world with all biomes.

### template_meadows
- PLAINS, MEADOW, CHERRY_GROVE
- FOREST, BIRCH_FOREST, DARK_FOREST, FLOWER_FOREST
- SWAMP, MANGROVE_SWAMP
- RIVER, BEACH

### template_desert
- DESERT, SAVANNA, WINDSWEPT_SAVANNA
- JUNGLE, BAMBOO_JUNGLE
- BADLANDS, WOODED_BADLANDS, ERODED_BADLANDS, WINDSWEPT_HILLS
- RIVER, BEACH

### template_snow
- SNOWY_PLAINS, ICE_SPIKES
- SNOWY_TAIGA, TAIGA, OLD_GROWTH_SPRUCE_TAIGA
- JAGGED_PEAKS, FROZEN_PEAKS, SNOWY_SLOPES
- FROZEN_RIVER, BEACH

## Configuration

Main configuration file: `plugins/WorldSystem/config.yml`

### World Template Configuration

You can configure custom biome templates:

```yaml
worldtemplates:
  multi_choose: true
  default: 'template_default'
  templates:
    1:
      name: 'template_desert'
      permission: ws.template.desert
      cost: 100
      generator:
        biomes:
          DESERT: 10
          SAVANNA: 5
          JUNGLE: 3
          # Higher weight = more territory
```

### Biome Weights

Each biome can have a weight (1-10+). Higher weight = larger territory.

## Permissions

| Permission | Description |
|------------|-------------|
| `ws.*` | All permissions |
| `ws.get` | Create a world |
| `ws.get.admin` | Create world for other players |
| `ws.delete` | Delete worlds |
| `ws.tp` | Teleport to other worlds |
| `ws.tp.other` | Teleport other players |
| `ws.build` | Build on other worlds |
| `ws.gamemode` | Change gamemode |
| `ws.template.<name>` | Use specific template |

## Supported Versions

- Minecraft 1.21.10 (and possibly 1.21.x)
- Paper/Spigot server required

## Dependencies

- **WorldEdit** (required) - For world management features
- **PlaceholderAPI** (optional) - For placeholders
- **Vault** (optional) - For economy integration
- **Chunky** (optional) - For async world loading

## Installation

1. Download the latest release
2. Place the JAR file in your server's `plugins` folder
3. Restart or reload the server
4. Configure templates in `config.yml` if needed

## Support

For issues and feature requests, please create an issue on GitHub.

## License

This plugin is distributed under the same license as the original WorldSystem plugin.

## Authors

- Butzlabben
- Trainerlord
- Cycodly
