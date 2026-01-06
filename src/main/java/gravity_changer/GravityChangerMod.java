package gravity_changer;

import com.mojang.logging.LogUtils;
import gravity_changer.api.RotationParameters;
import gravity_changer.command.DirectionArgumentType;
import gravity_changer.command.GravityCommand;
import gravity_changer.command.LocalDirectionArgumentType;
import gravity_changer.config.GravityChangerConfig;
import gravity_changer.item.GravityAnchorItem;
import gravity_changer.item.GravityChangerItem;
import gravity_changer.item.GravityChangerItemAOE;
import gravity_changer.mob_effect.GravityDirectionMobEffect;
import gravity_changer.mob_effect.GravityInvertMobEffect;
import gravity_changer.mob_effect.GravityPotion;
import gravity_changer.mob_effect.GravityStrengthMobEffect;
import gravity_changer.plating.GravityPlatingBlock;
import gravity_changer.plating.GravityPlatingBlockEntity;
import gravity_changer.plating.GravityPlatingItem;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.event.ConfigSerializeEvent;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GravityChangerMod implements ModInitializer {
    public static final String NAMESPACE = "gravity_changer";
    public static final Logger LOGGER = LogManager.getLogger(GravityChangerMod.class);
    
    public static ItemGroup GravityChangerGroup;
    
    public static ConfigHolder<GravityChangerConfig> configHolder;
    public static GravityChangerConfig config;

    @Override
    public void onInitialize() {
        GravityChangerItem.init();
        GravityChangerItemAOE.init();
        GravityAnchorItem.init();
        //TODO: Rewrite Gravity Plates to use components, not worth it for
        // the current version when I'm going to throw them out anyways (in favor of amethyst gravity)
        //ModComponents.initialize();
        
        AutoConfig.register(GravityChangerConfig.class, GsonConfigSerializer::new);
        configHolder = AutoConfig.getConfigHolder(GravityChangerConfig.class);
        configHolder.registerSaveListener(new ConfigSerializeEvent.Save<GravityChangerConfig>() {
            @Override
            public ActionResult onSave(ConfigHolder<GravityChangerConfig> configHolder, GravityChangerConfig gravityChangerConfig) {
                RotationParameters.updateDefault();
                return ActionResult.PASS;
            }
        });
        config = configHolder.getConfig();
        
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher)
        );
        
        GravityChangerGroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GravityChangerItem.GRAVITY_CHANGER_UP))
            .entries((enabledFeatures, entries) -> {
                entries.add(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_UP));
                entries.add(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_DOWN));
                entries.add(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_EAST));
                entries.add(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_WEST));
                entries.add(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_NORTH));
                entries.add(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_SOUTH));
                
                entries.add(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_UP_AOE));
                entries.add(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_DOWN_AOE));
                entries.add(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_EAST_AOE));
                entries.add(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_WEST_AOE));
                entries.add(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_NORTH_AOE));
                entries.add(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_SOUTH_AOE));
                
                entries.add(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 1)
                ));
                /*entries.add(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 2)
                ));
                entries.add(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 8)
                ));
                entries.add(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 32)
                ));
                entries.add(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 64)
                ));
                entries.add(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(false, 8)
                ));
                entries.add(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(false, 32)
                ));*/
                
                for (GravityAnchorItem item : GravityAnchorItem.ITEM_MAP.values()) {
                    entries.add(new ItemStack(item));
                }
                
                // gravity potions are both in food tab and gravity changer tab
                //Item[] potionItems = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};

                //TODO: Implement potion items, old way was removed
                /*for (Item potionItem : potionItems) {
                    for (Potion potion : GravityPotion.ALL) {
                        ItemStack stack = PotionUtil.setPotion(new ItemStack(potionItem), potion);
                        entries.add(stack);
                    }
                }*/
            })
            .displayName(Text.translatable("itemGroup.gravity_changer.general"))
            .build();
        
        Registry.register(
            Registries.ITEM_GROUP, id("general"),
            GravityChangerGroup
        );
        
        GravityDirectionMobEffect.init();
        GravityInvertMobEffect.init();
        GravityStrengthMobEffect.init();
        //GravityPotion.init();
        
        GravityPlatingBlock.init();
        GravityPlatingItem.init();
        GravityPlatingBlockEntity.init();
        
        DirectionArgumentType.init();
        LocalDirectionArgumentType.init();
    }
    
    public static Identifier id(String path) {
        return Identifier.of(NAMESPACE, path);
    }
}
