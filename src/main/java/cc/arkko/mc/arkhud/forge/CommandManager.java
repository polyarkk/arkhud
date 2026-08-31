package cc.arkko.mc.arkhud.forge;

import cc.arkko.mc.arkhud.forge.configs.ConfigManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommandManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("arkhud");

        registerCommandConfig(builder, "disabled", boolean.class, () -> ConfigManager.disabled, ConfigManager::setDisabled);
        registerCommandConfig(builder, "hideArmor", boolean.class, () -> ConfigManager.hideArmor, ConfigManager::setHideArmor);
        registerCommandConfig(builder, "potionEffects", boolean.class, () -> ConfigManager.potionEffects, ConfigManager::setPotionEffects);

        dispatcher.register(builder);
    }

    private static <TValue> void registerCommandConfig(
        LiteralArgumentBuilder<CommandSourceStack> builder, String name, Class<TValue> clazz, Supplier<TValue> getter, Consumer<TValue> setter
    ) {
        LOGGER.info("registerCommandConfig <- {}", name);

        ArgumentType<?> argumentType;

        if (clazz == boolean.class || clazz == Boolean.class) {
            argumentType = BoolArgumentType.bool();
        } else if (clazz == double.class || clazz == Double.class) {
            argumentType = DoubleArgumentType.doubleArg();
        } else if (clazz == float.class || clazz == Float.class) {
            argumentType = FloatArgumentType.floatArg();
        } else if (clazz == int.class || clazz == Integer.class) {
            argumentType = IntegerArgumentType.integer();
        } else if (clazz == long.class || clazz == Long.class) {
            argumentType = LongArgumentType.longArg();
        } else if (clazz == String.class) {
            argumentType = StringArgumentType.string();
        } else {
            throw new UnsupportedOperationException();
        }

        Function<CommandContext<?>, TValue> argumentValueSupplier = getArgumentValueSupplier("_" + name, argumentType);

        builder.then(
            Commands.literal(name)
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal(String.format("ConfigManager [%s]: %s", name, getter.get())), false);

                    return 1;
                })
                .then(Commands.argument("_" + name, argumentType).executes(ctx -> {
                    TValue v = argumentValueSupplier.apply(ctx);

                    setter.accept(v);

                    ctx.getSource().sendSuccess(() -> Component.literal(String.format("Set config [%s] to: %s", name, getter.get())), false);

                    return 1;
                }))
        );
    }

    @SuppressWarnings("unchecked")
    public static <TContext extends CommandContext<?>, TValue>
    Function<TContext, TValue> getArgumentValueSupplier(String name, ArgumentType<?> argumentType) {
        if (argumentType instanceof BoolArgumentType) {
            return ctx -> (TValue)(Boolean)BoolArgumentType.getBool(ctx, name);
        } else if (argumentType instanceof DoubleArgumentType) {
            return ctx -> (TValue)(Double)DoubleArgumentType.getDouble(ctx, name);
        } else if (argumentType instanceof FloatArgumentType) {
            return ctx -> (TValue)(Float)FloatArgumentType.getFloat(ctx, name);
        } else if (argumentType instanceof IntegerArgumentType) {
            return ctx -> (TValue)(Integer)IntegerArgumentType.getInteger(ctx, name);
        } else if (argumentType instanceof LongArgumentType) {
            return ctx -> (TValue)(Long)LongArgumentType.getLong(ctx, name);
        } else if (argumentType instanceof StringArgumentType) {
            return ctx -> (TValue)StringArgumentType.getString(ctx, name);
        }

        throw new UnsupportedOperationException();
    }
}
