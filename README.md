**Slowcraft is a library for creating low-tech, progressive crafting items called SlowTools.**

Inspired by the [Knitting](https://wiki.btwce.com/view/Knitting) system in [Better Than Wolves](https://wiki.btwce.com/view/Main_Page) and the [Polishing](https://create.fandom.com/wiki/Sand_Paper) system in [Create](https://modrinth.com/mod/create/).

![An example SlowTool](https://cdn.modrinth.com/data/cached_images/dde960135a26d11b97b1a0314503532468b461d3.png)

## Requirements

- [Fabric API](https://modrinth.com/mod/fabric-api)
- (Optional) [JEI](https://modrinth.com/mod/jei)

Slowcraft is a library. Installing it by itself does not add anything; mods, modpacks, and datapacks use it to create their own SlowTools.

![A SlowTool being completed over time](https://cdn.modrinth.com/data/lK5knzE0/images/3c5946f8424aec74266ebe6264dd4ac74678c8f9.gif)

## Documentation

- [Slowcraft Wiki](https://github.com/nibbyy/Slowcraft/wiki)
- [Making SlowTools](https://github.com/nibbyy/Slowcraft/wiki/Making-SlowTools)
- [Commands and Integrations](https://github.com/nibbyy/Slowcraft/wiki/Commands-and-Integrations)
- [Example Datapack](https://github.com/nibbyy/Slowcraft/releases/tag/Datapack)

## SlowTools

SlowTools are an intermediary crafting item, intended to be time-consuming crafts. The player holds right-click to work on it while its durability bar shows the current progress.

Progress is saved when the player stops, so a long craft can be completed a little at a time. Once the bar finishes, the SlowTool is consumed and its outputs are given to the player.

_A real life example could include things like knitting, weaving, or whittling._

## Made for Modpacks, Datapacks and Mods

Slowcraft supports two ways to create SlowTools:

- **Data-driven SlowTools** can be added through datapack JSON files. This is the recommended option for modpack and datapack authors.
- **Java-driven SlowTools** are registered items for mod developers who want custom sounds, assets, or additional Java behavior.

SlowTools can be configured with:

- A custom crafting time
- Up to four output stacks
- A custom name and appearance
- Optional creative-tab visibility
- Optional progressive-crafting overlays
- (Java) Custom usage and completion sounds

Slowcraft also includes a reusable `damage_tool_shapeless` recipe type for recipes that return a damaged tool instead of consuming it.

## Integration

When [JEI](https://modrinth.com/mod/jei) is installed, Slowcraft adds a **Progressive Crafting** recipe category showing each SlowTool's outputs and crafting time.

Data-driven SlowTools are added automatically, and each visible SlowTool also receives an Information page.

![Slowcraft's Progressive Crafting category in JEI](https://cdn.modrinth.com/data/cached_images/5987d333a2cbc34c5fc8dbb741850ab0bf498f89.png)

**JEI is optional and is not required to use Slowcraft.**

Planned Integration:
- EMI
- KubeJS

## Credits

- **FlowerChild** and the **BTW CE 3.0 Community** for the inspiration and art assets
- [Ivangeevo](https://modrinth.com/user/ivangeevo) for assistance with item logic

_Slowcraft was originally created for a modpack of my own, but anyone is welcome to use it in their mods or modpacks._