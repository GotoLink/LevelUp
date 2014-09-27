== LevelUp ! 0.6 ==
* Fixed fishing exploit
* Duplicated ores from Mining skill can no longer be placed
* Mining and Woodcutting skills now require correct tools for additional drops
* Fixed a potential issue with custom furnaces burning being blocked

== LevelUp ! 0.5 ==
* Added crop blacklist option using blocks internal names for farming skill

== LevelUp ! 0.4 ==
* Fixed talisman crafting
* Fixed server thingies
* Stuff noone cares about but me

== LevelUp ! 0.3 ==
* Updated for 1.7.2
* Fixed furnace access issue
* Added chinese locals from github, courtesy of m9731526

== LevelUp ! 0.3 ==
* Use Forge breaking block hook, for digging and mining detection (code optimisation)
* Improved packet checking for setting skill points (code fixing)

== LevelUp ! 0.2 ==
* Force fixed crash on player data outdatedness
* Fixed player skills disappearing on death
* Removed Skill Gui ability to reset points
* Added class reset to Book of Unlearning, toggeable in config file
* Lowered amount of experience bonus on mining ores
* Changed config names
* Fixed crash while fishing things
* Added config for points given per level
* Fixed farming weirdness for modded plants
* Added more support for ground blocks while digging
* Added more support for eadible items while cooking
* Changed woodcutting to drop corresponding planks from log if craftable. Default to vanilla planks if not.

== LevelUp ! 0.1 ==
* Port to Forge
* Obfuscated "version* independent"
* Removed all base class edits
* Removed mob buffs (too close to vanilla)
* Added SMP support
* Added mcmod.info and pack.mcmeta files
* Added config file for item ids
* Added custom key for opening GUIs (default to L)
* Added simple text HUD (toggeable in config file, with the original HUD)
* Added all ores (and support for custom ones) into Mining bonus
* Changed: Cooking and Smelting time bonus, require to have the furnace GUI opened, and scaled along skill levels (every 10, increased chance of bonus)
* Changed: Farming bonus requires the farmer to be close to his crops, depending on his farming skill level (higher for greater range)