# Builder's Utils: <Commands> (BTW:CE Addon)
## Features
Revamp and add useful commands to the game such as using actual items, entities, potion and enchant name instead of ID numbers.

### Commands
- /give <player> <ID|name> [count] [meta]

- /summon <entity:...> [x/y/z] [count] : a ":" can be added to the entity name to add rider (exemple: "/summon Bat:Creeper 5/70/5 4" will summon 4 creeper riding a bat at the coords), as many ":" can be added.

- /enchant <player> <ID|name> [level]

- /effect <player> <set|clear> <ID|name> [seconds] [amplifier]

- /kill <player|entity|item|all> : delete specified entity (if "noDrop" is added, the deleted entity won't drop its inventory/loot)
  - /kill <player> <name> [noDrop]
  - /kill <entity> [name] [noDrop] : if the name of the entity is specified it will only kill this type of entity (exemple: "/kill entity Creeper" will only kill creepers)
  - /kill <item>
  - /kill <all> [noDrop] : delete every entity and item

[] = optional

<> = needed

| = OR

### Works in multiplayer

## Supported languages:
- English

## Found a bug or have suggestions ?

https://github.com/BTW-Community/Builders-Utils-Commands

## Download: 
https://modrinth.com/mod/builders-utils-commands/versions
