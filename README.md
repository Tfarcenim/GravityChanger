# GravityChanger

NOTICE: I am extremely unlikely to maintain this fork, so if anyone else wants
feel free to do so, if I ever come back I'll probably be working off the most 
up-to-date version of the mod anyway.

Normally I would have just submitted this as a pull req on the GitHub for qouteall's
fork, but I wanted to use the yarn mapping to port this since I was more used to
using it and with this version using a different mapping I felt like it made
more sense as a fork than sending a pull req with literally every single file significantly
changed (but I also did that anyways).

If anyone wants to convert it back to mojmap+parchment mappings and submit it to
qouteall's GitHub, feel free, but the mixins will probably be annoying, so they will
probably all have to be replaced anyway.

Regardless, this fork is close enough to qouteall's fork that this can be considered the same
thing, so the below information has mostly not changed except for adding some history to the fork notice
since there was a lot more to it.

#
This is a fork of qouteall's [Gravity Changer](https://github.com/qouteall/GravityChanger).

Which is a fork of FusionFlux's [Gravity API](https://github.com/Fusion-Flux/Gravity-Api) for Fabric.

Gravity API was partially a fork of Gaider10's [Gravity Changer](https://github.com/Gaider10/GravityChanger)
(Some code was also adapted from forge from the below two mods, mods like pehkui,
written from scratch, or adapted from things like the Dinnerbone code).

The 1.12+ generation of gravity mods seem to be partially based on code from Mysteryem's [Up and Down and All Around](https://github.com/Mysteryem/Up_And_Down_And_All_Around).

As far as I know, most gravity mods past 1.7.10 are attempts to recreate the gravity effect
from what I consider the original gravity mod, [StarMiner](https://web.archive.org/web/20160215085700/http://forum.minecraftuser.jp/viewtopic.php?f=13&t=17975).

However, there was at least one earlier gravity mod for 1.2.5 called [GravityCraft](https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/1285013-1-2-5-gravitycraft-mineup-1-1-higher-worlds#History) that may have inspired others as some features seem familiar.


If there are missing gravity mods, please let me know.


#

Maintaining the fork because Immersive Portals mod depends on the Gravity changing mod, 
and Fabric mod cannot depend on a Quilt mod. (For this fork specifically, compatibility does
not work out of the box, supposedly somebody in the discord has a fix that just requires changing
the modid [Link to Discord Message](https://discord.com/channels/662271867431682058/1143860421229563934/1386945825233436702), but I have yet to get to this)

This Gravity Changer mod is not identical to Fusion's Gravity API.
**The two mods cannot be used interchangeably.**

### Added features

* Gravity effects and potions.
* Gravity anchor. Changes gravity when held. (Some resources come from [AmethystGravity](https://modrinth.com/mod/amethyst-gravity) by CyborgCabbage)
* Gravity plating. Generates gravity field. Allows adjusting effect range. (Some code and resources come from AmethystGravity)

The gravity potions, anchors and plating are not obtainable from normal survival.

### Commands

This mod's commands are different to Gravity API's. These are the commands in the latest version of this mod:

`/gravity set_base_direction <direction> [entities]` sets the base gravity direction. (The base direction can be overridden by other things including effects, gravity anchor and gravity plating). Without `[entities]` argument it will target the command sender (the same applies to all commands). Examples: `/gravity set_base_direction up`   `/gravity set_base_direction up @e[type=!minecraft:player]`

`/gravity set_base_strength <strength> [entities]` sets the base gravity strength. The strength effects will multiply on the base strength (instead of overriding it). Examples: `/gravity set_base_strength 0.5`  `/gravity set_base_strength 0.5 @e`

`/gravity view` shows the base gravity direction and strength of the command sender.

`/gravity reset [entities]` reset the base gravity direction and strength. 

`/gravity randomize_base_direction [entities]` sets the base direction as a random direction.

`/gravity set_relative_base_direction <relativeDirection> [entities]` sets the gravity direction as a direction relative to the entity's viewing direction. The `<relativeDirection>` can be `forward`, `backward`, `left`, `right`, `up` or `down`.

`/gravity set_dimension_gravity_strength <strength>` sets the dimensional gravity strength for the current dimension. 

`/gravity view_dimension_info` shows the dimensional gravity strength for the current dimension.

### What Entities Can Change Gravity

By default, all living entities, projectiles and minecarts can change gravity.

For other entity types, the entity types that are in tag `gravity_changer:allowed_special` can change gravity.

### How to use the API

#### Add dependency

Add this into `repositories`
```
maven { url 'https://jitpack.io' }
```

Add this into `dependencies`
```
modImplementation("com.github.qouteall:GravityChanger:v1.0.2-mc1.20.1")
```

See [JitPack](https://jitpack.io/#qouteall/GravityChanger)

### Future development goal

* Explore the possibility of using world-coordinate velocity (although it requires more mixin it's easier to debug and maintain) instead of entity-local velocity to simplify future development. Do aggressive rewrite (simpler and faster than many mixins) to MC collision code and use Lithium's collision code which is faster.

