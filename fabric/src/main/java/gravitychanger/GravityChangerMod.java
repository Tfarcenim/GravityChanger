package gravitychanger;

import gravitychanger.api.RotationParameters;
import gravitychanger.command.DirectionArgumentType;
import gravitychanger.command.GravityCommand;
import gravitychanger.command.LocalDirectionArgumentType;
import gravitychanger.config.GravityChangerConfig;
import gravitychanger.item.GravityAnchorItem;
import gravitychanger.item.GravityChangerItem;
import gravitychanger.item.GravityChangerItemAOE;
import gravitychanger.mob_effect.refined.GravityDirectionStatusEffect;
import gravitychanger.mob_effect.refined.GravityInvertStatusEffect;
import gravitychanger.mob_effect.refined.GravityPotions;
import gravitychanger.mob_effect.refined.GravityStrengthStatusEffect;
import gravitychanger.plating.GravityPlatingBlock;
import gravitychanger.plating.GravityPlatingBlockEntity;
import gravitychanger.plating.GravityPlatingItem;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GravityChangerMod implements ModInitializer {
    public static final String NAMESPACE = "gravitychanger";
    public static final Logger LOGGER = LogManager.getLogger(GravityChangerMod.class);
    
    public static CreativeModeTab GravityChangerGroup;
    
    public static ConfigHolder<GravityChangerConfig> configHolder;
    public static GravityChangerConfig config;
    
    @Override
    public void onInitialize() {
        GravityChangerItem.init();
        GravityChangerItemAOE.init();
        GravityAnchorItem.init();
        LOGGER.info("[gravity-changer]");
        
        AutoConfig.register(GravityChangerConfig.class, GsonConfigSerializer::new);
        configHolder = AutoConfig.getConfigHolder(GravityChangerConfig.class);
        configHolder.registerSaveListener((configHolder, gravityChangerConfig) -> {
            RotationParameters.updateDefault();
            return InteractionResult.PASS;
        });
        config = configHolder.getConfig();
        
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> GravityCommand.register(dispatcher)
        );
        
        GravityChangerGroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(GravityChangerItem.GRAVITY_CHANGER_UP))
            .displayItems((enabledFeatures, entries) -> {
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_UP));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_DOWN));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_EAST));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_WEST));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_NORTH));
                entries.accept(new ItemStack(GravityChangerItem.GRAVITY_CHANGER_SOUTH));
                
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_UP_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_DOWN_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_EAST_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_WEST_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_NORTH_AOE));
                entries.accept(new ItemStack(GravityChangerItemAOE.GRAVITY_CHANGER_SOUTH_AOE));
                
                entries.accept(GravityPlatingItem.createStack(
                    new GravityPlatingBlockEntity.SideData(true, 1)
                ));
//                entries.accept(GravityPlatingItem.createStack(
//                    new GravityPlatingBlockEntity.SideData(true, 2)
//                ));
//                entries.accept(GravityPlatingItem.createStack(
//                    new GravityPlatingBlockEntity.SideData(true, 8)
//                ));
//                entries.accept(GravityPlatingItem.createStack(
//                    new GravityPlatingBlockEntity.SideData(true, 32)
//                ));
//                entries.accept(GravityPlatingItem.createStack(
//                    new GravityPlatingBlockEntity.SideData(true, 64)
//                ));
//                entries.accept(GravityPlatingItem.createStack(
//                    new GravityPlatingBlockEntity.SideData(false, 8)
//                ));
//                entries.accept(GravityPlatingItem.createStack(
//                    new GravityPlatingBlockEntity.SideData(false, 32)
//                ));
//
                for (GravityAnchorItem item : GravityAnchorItem.ITEM_MAP.values()) {
                    entries.accept(new ItemStack(item));
                }
                
                // gravity potions are both in food tab and gravity changer tab
                Item[] potionItems = new Item[]{Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION};

                for (Item potionItem : potionItems) {
                    for (Potion potion : GravityPotions.ALL) {
                        Holder<Potion> holder = new Holder.Direct<>(potion);
                        ItemStack stack = PotionContents.createItemStack(potionItem, holder);
                        entries.accept(stack);
                    }
                }
            })
            .title(Component.translatable("itemGroup.gravity_changer.general"))
            .build();
        
        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, id("general"),
            GravityChangerGroup
        );

        GravityDirectionStatusEffect.init();
        GravityStrengthStatusEffect.init();
        GravityInvertStatusEffect.init();
        GravityPotions.init();

//        GravityDirectionMobEffect.registerEffects();
//        GravityDirectionMobEffect.registerEvent();
//        GravityInvertMobEffect.init();
//        GravityStrengthMobEffect.init();
//        GravityPotion.init();
        
        GravityPlatingBlock.init();
        GravityPlatingItem.init();
        GravityPlatingBlockEntity.init();
        
        DirectionArgumentType.init();
        LocalDirectionArgumentType.init();
    }
    
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(NAMESPACE, path);
    }
}
