# Treasure Bags Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.19.4-1.8.0] - 2023-05-29
- Updated to Minecraft 1.19.4

## [1.19.3-1.8.0] - 2023-05-29
- Updated to Minecraft 1.19.3
### Added
- Chinese translation (yaodaosang) [#10]

## [1.8.0] - 2022-10-21
- Updated to Minecraft 1.19.2

## [1.7.0] - 2022-03-28
- Updated to Minecraft 1.18.2 (Mrbysco) [#7]

## [1.6.0] - 2021-12-05
- Updated to Minecraft 1.18

## [1.5.0] - 2021-07-31
- Updated to Minecraft 1.17.1

## [1.4.1] - 2021-07-23
### Changed
- Updated to official Mojang mappings and Silent Lib 4.10.x (required, will not work with 4.9.x)

## [1.4.0] - 2021-05-16
### Added
- Tag-based entity groups. Typing an entity type tag's ID in the `dropsFromGroups` of a bag type will now make it drop from entities with that tag.
- (API) Custom entity groups can now be defined by registering an `IEntityGroup` with `TreasureBagsApi`

## [1.3.2] - 2021-04-08
### Added
- Bag groups and a config option to disable bag types by group [#6]
### Changed
- Improved logging of errored bag types
- Switched config to use Forge config system

## [1.3.1] - 2020-12-17
### Changed
- Add `killed_by_player` condition to player entity group loot table
- Entity group loot tables are now type `minecraft:entity`
- Increased rolls on boss loot table from 1 to 2

## [1.3.0] - 2020-12-04
Updated for 1.16.3/4

## [1.2.1] - 2020-03-06
### Added
- Config to always drop loot as item entities (Partonetrain) [#4]
- zh_cn.json lang file (XuyuEre) [#2]

## [1.2.0] - 2020-02-03
Ported to Minecraft 1.15.2

## [1.1.2] - 2019-09-14
### Fixed
- "Entity already tracked" crash

## [1.1.1] - 2019-08-06
Updated for Forge 28.0.45

## [1.1.0] - 2019-07-23
### Added
- Bag types that fail to load or have missing/invalid loot tables are now counted and mentioned to players when they log in. This should make data pack issues more obvious.
- A loot table (`treasurebags:starting_inventory`) which is used once when a player first joins a server. By default, this gives a single treasure bag of type `treasurebags:spawn`. Modifying the bag's loot table (`treasurebags:bags/spawn`) is recommended if you want to give new players items, but you could modify the starting inventory table if you prefer.

## [1.0.0] - 2019-07-01
Updated to 1.14.3

## [0.4.0] - 2019-06-24
JEI plugin is up to date. Recommended Forge is 26.0.51 or later.
### Added
- New layer to treasure bag model. Color can be set with `bagStringColor`.
### Changed
- Cleanup up log output

## [0.3.3] - 2019-06-20
### Fixed
- Entity group loot tables not working

## [0.3.2] - 2019-06-17
Update for Silent Lib 4.1.1

## [0.3.1] - 2019-06-11
Updated to Minecraft 1.14.2

## [0.3.0] - 2019-05-08
### Added
- Treasure bags can now be used as ingredients in recipes. See wiki for details.
- Shaped and shapeless recipe types for treasure bags. This allows you to create recipes which craft specific treasure bags. You can, of course, combine this with treasure bag ingredients as well.
- JEI can distinguish treasure bags of different types
### Changed
- Treasure bags now display the data pack's ID in the tooltip. Unknown/invalid bags are also indicated in the tooltip.
### Fixed
- Bag types should now synchronize with connecting clients on both dedicated servers and LAN games

## [0.2.0] - 2019-03-31
### Added
- Opening a bag while sneaking will now open the entire stack
- Command, `/treasurebags give` which will give players treasure bags
- Command, `/treasurebags list` which will list all bag type ID's
- Loot tables for entity groups. These will make adding bag drops easier. These are located at `data/treasurebags/loot_tables/entity_group`. The groups are hostile (monster), peaceful (animals), boss, and player.
### Changed
- Like items from a bag will get stacked together (less chat spam) 

## [0.1.0] - 2019-03-28
First release
