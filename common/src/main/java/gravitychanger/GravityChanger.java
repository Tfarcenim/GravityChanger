package gravitychanger;

import gravitychanger.api.GravityChangerAPI;
import gravitychanger.api.RotationParameters;
import gravitychanger.attachments.CommonDataAttachments;
import gravitychanger.command.DirectionArgumentType;
import gravitychanger.command.LocalDirectionArgumentType;
import gravitychanger.config.GravityChangerConfig;
import gravitychanger.init.ModCreativeTabs;
import gravitychanger.init.ModItems;
import gravitychanger.mob_effect.GravityDirectionMobEffect;
import gravitychanger.mob_effect.GravityInvertMobEffect;
import gravitychanger.mob_effect.refined.GravityPotions;
import gravitychanger.mob_effect.refined.GravityStrengthMobEffect;
import gravitychanger.plating.GravityPlatingBlock;
import gravitychanger.plating.GravityPlatingBlockEntity;
import gravitychanger.plating.GravityPlatingItem;
import gravitychanger.util.RotationUtil;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class GravityChanger {

    public static final String MOD_NAME = "GravityChanger";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final String MOD_ID = "gravitychanger";
    public static final ResourceLocation DATA_COMPONENT_ID =
        id("gravity_data");
    public static final ResourceLocation DIMENSION_DATA_ID =
        id("dimension_data");
    public static ConfigHolder<GravityChangerConfig> configHolder;
    public static GravityChangerConfig config;

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {
        AutoConfig.register(GravityChangerConfig.class, GsonConfigSerializer::new);
        GravityChanger.configHolder = AutoConfig.getConfigHolder(GravityChangerConfig.class);
        GravityChanger.configHolder.registerSaveListener((configHolder, gravityChangerConfig) -> {
            RotationParameters.updateDefault();
            return InteractionResult.PASS;
        });
        GravityChanger.config = GravityChanger.configHolder.getConfig();
    }

    public static void register() {
        GravityStrengthMobEffect.init();
        GravityInvertMobEffect.init();
        GravityDirectionMobEffect.init();

        ModCreativeTabs.init();
        GravityPotions.init();

        ModItems.init();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,"side_data", GravityPlatingItem.SIDE_DATA_COMPONENT);

        ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE,GravityChanger.id("direction").toString(),
                DirectionArgumentType.class,SingletonArgumentInfo.contextFree(() -> DirectionArgumentType.instance));

        ArgumentTypeInfos.register(BuiltInRegistries.COMMAND_ARGUMENT_TYPE,GravityChanger.id("local_direction").toString(),
                LocalDirectionArgumentType.class,SingletonArgumentInfo.contextFree(() -> LocalDirectionArgumentType.instance));


        Registry.register(
                BuiltInRegistries.BLOCK, GravityChanger.id("gravity_plating"), GravityPlatingBlock.PLATING_BLOCK
        );

        Registry.register(BuiltInRegistries.ITEM, GravityChanger.id("gravity_plating"),
                GravityPlatingItem.PLATING_BLOCK_ITEM
        );
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, GravityChanger.id("gravity_plating"),GravityPlatingBlockEntity.TYPE);
        CommonDataAttachments.init();
    }

    public static Vec3 redirection(Vec3 movement, AABB entityBoundingBox, List<VoxelShape> collisions, Entity entity) {
        Direction gravityDirection;
        if (entity == null || (gravityDirection = GravityChangerAPI.getGravityDirection(entity)) == Direction.DOWN) {
            return Entity.collideWithShapes(movement, entityBoundingBox, collisions);
        }

        Vec3 playerMovement = RotationUtil.vecWorldToPlayer(movement, gravityDirection);
        double playerMovementX = playerMovement.x;
        double playerMovementY = playerMovement.y;
        double playerMovementZ = playerMovement.z;
        Direction directionX = RotationUtil.dirPlayerToWorld(Direction.EAST, gravityDirection);
        Direction directionY = RotationUtil.dirPlayerToWorld(Direction.UP, gravityDirection);
        Direction directionZ = RotationUtil.dirPlayerToWorld(Direction.SOUTH, gravityDirection);
        if (playerMovementY != 0.0D) {
            playerMovementY = Shapes.collide(directionY.getAxis(), entityBoundingBox, collisions, playerMovementY * directionY.getAxisDirection().getStep()) * directionY.getAxisDirection().getStep();
            if (playerMovementY != 0.0D) {
                entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(0.0D, playerMovementY, 0.0D, gravityDirection));
            }
        }

        boolean isZLargerThanX = Math.abs(playerMovementX) < Math.abs(playerMovementZ);
        if (isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = Shapes.collide(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getAxisDirection().getStep()) * directionZ.getAxisDirection().getStep();
            if (playerMovementZ != 0.0D) {
                entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(0.0D, 0.0D, playerMovementZ, gravityDirection));
            }
        }

        if (playerMovementX != 0.0D) {
            playerMovementX = Shapes.collide(directionX.getAxis(), entityBoundingBox, collisions, playerMovementX * directionX.getAxisDirection().getStep()) * directionX.getAxisDirection().getStep();
            if (!isZLargerThanX && playerMovementX != 0.0D) {
                entityBoundingBox = entityBoundingBox.move(RotationUtil.vecPlayerToWorld(playerMovementX, 0.0D, 0.0D, gravityDirection));
            }
        }

        if (!isZLargerThanX && playerMovementZ != 0.0D) {
            playerMovementZ = Shapes.collide(directionZ.getAxis(), entityBoundingBox, collisions, playerMovementZ * directionZ.getAxisDirection().getStep()) * directionZ.getAxisDirection().getStep();
        }
        return new Vec3(playerMovementX, playerMovementY, playerMovementZ);
        //return RotationUtil.vecPlayerToWorld(playerMovementX, playerMovementY, playerMovementZ, gravityDirection);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,path);
    }
}