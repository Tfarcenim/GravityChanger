package gravity_changer.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import gravity_changer.GravityComponent;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.GCUtil;
import gravity_changer.util.RotationUtil;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class GravityCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager
            .literal("gravity")
            .requires(source -> source.hasPermissionLevel(2));
        
        builder.then(CommandManager.literal("set_base_direction")
            .then(CommandManager.argument("direction", DirectionArgumentType.instance)
                .executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    Validate.isTrue(entity != null);
                    Direction direction = DirectionArgumentType.getDirection(context, "direction");
                    GravityChangerAPI.setBaseGravityDirection(entity, direction);
                    return 1;
                })
                .then(CommandManager.argument("entities", EntityArgumentType.entities())
                    .executes(context -> {
                        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                        Direction direction = DirectionArgumentType.getDirection(context, "direction");
                        for (Entity entity : entities) {
                            GravityChangerAPI.setBaseGravityDirection(entity, direction);
                        }
                        return entities.size();
                    })
                )
            )
        );
        
        builder.then(CommandManager.literal("reset")
            .executes(context -> {
                Entity entity = context.getSource().getEntity();
                Validate.isTrue(entity != null);
                GravityChangerAPI.resetGravity(entity);
                return 1;
            })
            .then(CommandManager.argument("entities", EntityArgumentType.entities())
                .executes(context -> {
                    Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                    for (Entity entity : entities) {
                        GravityChangerAPI.resetGravity(entity);
                    }
                    return entities.size();
                })
            )
        );
        
        builder.then(CommandManager.literal("set_base_strength")
            .then(CommandManager.argument("strength", DoubleArgumentType.doubleArg(-20, 20))
                .executes(context -> {
                    Entity entity = context.getSource().getEntity();
                    Validate.isTrue(entity != null);
                    double strength = DoubleArgumentType.getDouble(context, "strength");
                    return executeSetBaseStrength(List.of(entity), strength);
                })
                .then(CommandManager.argument("entities", EntityArgumentType.entities())
                    .executes(context -> {
                        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                        double strength = DoubleArgumentType.getDouble(context, "strength");
                        return executeSetBaseStrength(entities, strength);
                    })
                )
            )
        );
        
        builder.then(CommandManager.literal("view")
            .executes(context -> {
                Entity entity = context.getSource().getEntity();
                
                GravityComponent component = GravityChangerAPI.getGravityComponent(entity);
                
                context.getSource().sendFeedback(
                    () -> Text.translatable(
                        "gravity_changer.command.inform",
                        component.getBaseGravityDirection().getName(),
                        component.getBaseGravityStrength()
                    ), false
                );
                
                return 0;
            })
        );
        
        builder.then(CommandManager.literal("randomize_base_direction")
            .executes(context -> {
                ServerCommandSource source = context.getSource();
                Entity entity = source.getEntity();
                Validate.isTrue(entity != null);
                return executeRandomizeBaseDirection(source, List.of(entity));
            })
            .then(CommandManager.argument("entities", EntityArgumentType.entities())
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                    return executeRandomizeBaseDirection(source, entities);
                })
            )
        );
        
        builder.then(CommandManager.literal("set_relative_base_direction")
            .then(CommandManager.argument("relativeDirection", LocalDirectionArgumentType.instance)
                .executes(context -> {
                    LocalDirection relativeDirection =
                        LocalDirectionArgumentType.getDirection(context, "relativeDirection");
                    
                    Entity entity = context.getSource().getEntity();
                    
                    Validate.isTrue(entity != null);
                    
                    return executeSetRelativeBaseDir(
                        context.getSource(), relativeDirection,
                        List.of(entity)
                    );
                })
                .then(CommandManager.argument("entities", EntityArgumentType.entities())
                    .executes(context -> {
                        LocalDirection relativeDirection =
                            LocalDirectionArgumentType.getDirection(context, "relativeDirection");
                        
                        Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                        
                        return executeSetRelativeBaseDir(
                            context.getSource(), relativeDirection,
                            entities
                        );
                    })
                )
            )
        );
        
        builder.then(CommandManager.literal("set_dimension_gravity_strength")
            .then(CommandManager.argument("strength", DoubleArgumentType.doubleArg(-20, 20))
                .executes(context -> {
                    ServerWorld world = context.getSource().getWorld();
                    double strength = DoubleArgumentType.getDouble(context, "strength");
                    GravityChangerAPI.setDimensionGravityStrength(world, strength);
                    return 0;
                })
            )
        );
        
        builder.then(CommandManager.literal("view_dimension_info")
            .executes(context -> {
                ServerWorld world = context.getSource().getWorld();
                double strength = GravityChangerAPI.getDimensionGravityStrength(world);
                context.getSource().sendFeedback(
                    () -> Text.translatable("gravity_changer.command.dimension_info", strength), false
                );
                return 0;
            })
        );
        
        dispatcher.register(builder);
    }
    
    private static int executeSetBaseStrength(Collection<? extends Entity> entities, double strength) {
        for (Entity entity : entities) {
            GravityChangerAPI.setBaseGravityStrength(entity, strength);
        }
        return entities.size();
    }
    
    private static int executeRandomizeBaseDirection(ServerCommandSource source, Collection<? extends Entity> entities) {
        Random random = source.getWorld().random;
        for (Entity entity : entities) {
            Direction gravityDirection = Direction.random(random);
            GravityChangerAPI.setBaseGravityDirection(entity, gravityDirection);
        }
        return entities.size();
    }
    
    private static void getSendFeedback(ServerCommandSource source, Entity entity, Direction gravityDirection) {
        Text text = GCUtil.getDirectionText(gravityDirection);
        if (source.getEntity() != null && source.getEntity() == entity) {
            source.sendFeedback(() -> Text.translatable("commands.gravity.get.self", text), true);
        }
        else {
            source.sendFeedback(() -> Text.translatable("commands.gravity.get.other", entity.getDisplayName(), text), true);
        }
    }
    
    private static int executeSetRelativeBaseDir(
        ServerCommandSource source, LocalDirection relativeDirection,
        Collection<? extends Entity> entities
    ) {
        int i = 0;
        for (Entity entity : entities) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            Direction combinedRelativeDirection = switch (relativeDirection) {
                case DOWN -> Direction.DOWN;
                case UP -> Direction.UP;
                case FORWARD, BACKWARD, LEFT, RIGHT ->
                    Direction.fromHorizontal(relativeDirection.getHorizontalOffset() + Direction.fromRotation(entity.getYaw()).getHorizontal());
            };
            Direction newGravityDirection = RotationUtil.dirPlayerToWorld(combinedRelativeDirection, gravityDirection);
            GravityChangerAPI.setBaseGravityDirection(entity, newGravityDirection);
            
            getSendFeedback(source, entity, newGravityDirection);
            i++;
        }
        return i;
    }
    
}
