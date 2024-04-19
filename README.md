# Expanded Ecosphere

William Wythers' Overhauled Overworld is a world-gen mod/pack that aims to re-create the vanilla overworld in a way that makes a more realistic and atmospheric world. You can explore more than 200 new/overhauled biomes which make your world insanely more immersive.

# Official Downloads
**Mods**:
[Modrinth](https://modrinth.com/mod/expanded-ecosphere)
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/expanded-ecosphere) 

**Datapacks**:
[1.19](https://www.planetminecraft.com/data-pack/william-wythers-overhauled-overworld/)
[1.18.1](https://www.planetminecraft.com/data-pack/william-wythers-overhauled-overworld-for-1-18-1/)

(Those are more WWOO then Expanded Ecosphere)
[1.17](https://www.planetminecraft.com/data-pack/william-wythers-overhauled-overworld-legacy-for-1-17/) 
[1.16](https://www.planetminecraft.com/data-pack/william-wythers-overhauled-overworld-1-17-snapshots-edition/)


# Features Expanded Ecosphere 1.19+
- Large Biomes! Because of some problems with world presets, you should not use the vanilla world presets. In the mod version you should definitely use the forceLargeBiomes option, found in the WWOO config.
Old versions or datapack: (Select the world type large biomes in your world creation menu, or on a server set `level-type` to `LARGE_BIOMES`)

### Mod only
- A config menu is available if you install [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) and on fabric also [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu).
- The overworld isn't piglin safe. (On datapack it is, so that the Hoglins which can spawn in the overworld don't mutate to Zoglins)

# Compatible Mods/Datapacks
If you know any other mods or datapacks that work well with Expanded Ecosphere, then contact me (Cristelknight) on [Discord](https://discord.gg/yJng7sC44x).

### World-gen Mods
| Mod Name  | Download | More Info |
| ------------- | ------------- | ------------- |
| Oh The Biomes You'll Go  | Curseforge: [Forge](https://www.curseforge.com/minecraft/mc-mods/oh-the-biomes-youll-go) [Fabric](https://www.curseforge.com/minecraft/mc-mods/oh-the-biomes-youll-go-fabric)  | Only compatible with the mod |
| The Graveyard Biomes | Curseforge: [Forge](https://www.curseforge.com/minecraft/mc-mods/the-graveyard-biomes-forge) [Fabric](https://www.curseforge.com/minecraft/mc-mods/the-graveyard-biomes-fabric) | Only compatible with the mod |
| Biomes O' Plenty  | [Curseforge](https://www.curseforge.com/minecraft/mc-mods/biomes-o-plenty)  | Only compatible with the mod |
| Promenade  | [Curseforge](https://www.curseforge.com/minecraft/mc-mods/promenade)  | Only compatible with the mod |
| Terralith  | [PlanetMC](https://www.planetminecraft.com/data-pack/terralith-overworld-evolved-100-biomes-caves-and-more/) [Curseforge](https://www.curseforge.com/minecraft/mc-mods/terralith)  | Only compatible with the mod, if compatible mode is enabled |

# FAQ
- Will there be a Forge 1.16.x version? No.
- Where can I find andesite? In andesite crags.
- Where do I find diorite? In Mediterranean Islands and Thermal Taigas.
- Where do beehives spawn? In desert lakes and desert river biomes.
- I can't find any sugar cane. Look in the tropical biomes and swamps.

# How to install
## On Client:
1. Download the mod or datapack version from the [downloads](#official-downloads) above.
##### Mod:
1. put the file into the `mods` folder in `.minecraft/mods`. 
2. Then install the matching mod loader for your downloaded mod file: [Fabric download](https://fabricmc.net/use/installer/)[(tutorial)](https://fabricmc.net/wiki/player:tutorials:install_mcl:windows) or [Forge download](https://files.minecraftforge.net/net/minecraftforge/forge/)[(tutorial)](https://www.youtube.com/watch?v=fMKwJ97ri90).
3. Then start your game and create a new world.

##### Datapack: 
1. Open Minecraft.
2. Create a new world and click on "Data Packs".
3. Drag the data pack into the Minecraft window. It should be a `.zip` file or `directory`.
Click "Yes" on the confirmation screen.
4. Move the data pack to the right hand side of the screen by clicking the triangle on its icon.
5. Click "Done" and continue creating your world, then click on "Create New World".

## On a server:
1. If you are using a Vanilla, Spigot, or Paper server, download the datapack version. If you are using a Fabric or Forge server, you can download either the datapack or the mod version ([download](#official-downloads) links are above).
2. If you are using the datapack version (the one that ends with `.zip`), put the file into the `world/datapacks` folder. If you are using the mod version (the one that ends with `.jar`), put the file into the `mods` folder.
3. Start your server.<br>**Next steps only needed in 1.18-1.19.2!**<br>If you log onto it, you'll see that the area around spawn is vanilla terrain while beyond it is new terrain. Stop your server.
4. Go into the `world` folder and delete the `region` folder.
5. Restart your server, and it should be fully generating new terrain
