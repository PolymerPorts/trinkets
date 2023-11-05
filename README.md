

# Trinkets
A data-driven accessory mod for Minecraft using Fabric.

## Polymer Ports
This fork/version of a mod is server side port (still Fabric!), for general modpack/singleplayer usage check [original version](https://github.com/emilyploszaj/trinkets).
To access trinkets ui, use `/trinkets` command or right-click on armor slots.

It's also fully compatible with regular Trinkets api, so you can use it with PolyMc + other Trinkets dependent mods

- With server resource pack (uses Polymer for generation, suggested, but requires additional setup): 
![With RP](https://imgur.com/xdK3Pe7.png)
- Without resource packs (default):
![Without RP](https://imgur.com/8LqfCft.png)

## About
Trinkets adds a slot group and slot system to Minecraft. Slot groups are collections of slots for a certain body part or more vague area. By default there are 6 slot groups (head, chest, legs, feet, offhand, hand) that can have slots added to them, but more groups can be added if desired. Trinkets' UI is intuitive to use, accessible, and attempts to do away with clutter. Its system means that you'll never have a slot that's not used for anything, as mods request the slots they want.

# Downloads:
- Modrinth: https://modrinth.com/mod/trinkets-polymer
- Github Releases: https://github.com/PolymerPorts/trinkets/releases

## Developers
To add Trinkets to your project you need to add jitpack and nerdhubmc to your repositories in your build.gradle
```
repositories {
	maven { url = "https://maven.terraformersmc.com/" }
	maven { url = "https://maven.nucleoid.xyz" }
	maven { url = "https://maven.ladysnake.org/releases" }
}
```
And then to add Trinkets you add it as a dependency in your build.gradle
```
dependencies {
	modImplementation "eu.pb4.polyport:trinkets:${trinkets_version}"
}
```

For basic tutorials and comprehensive documentation, visit this repository's [wiki](https://github.com/emilyploszaj/trinkets/wiki/Home).
