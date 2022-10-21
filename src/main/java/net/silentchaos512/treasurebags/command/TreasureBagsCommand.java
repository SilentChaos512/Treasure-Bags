package net.silentchaos512.treasurebags.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.lib.util.PlayerUtils;
import net.silentchaos512.treasurebags.lib.BagTypeManager;
import net.silentchaos512.treasurebags.lib.IBagType;
import net.silentchaos512.treasurebags.setup.TbItems;

import java.util.stream.Collectors;

public final class TreasureBagsCommand {
    private static final SuggestionProvider<CommandSourceStack> bagTypeSuggestions = (ctx, builder) ->
            SharedSuggestionProvider.suggestResource(BagTypeManager.getValues().stream().map(IBagType::getId), builder);

    private TreasureBagsCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("treasurebags")
                .requires(source -> source.hasPermission(2));

        // give
        builder.then(Commands.literal("give")
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("bagType", ResourceLocationArgument.id())
                                .suggests(bagTypeSuggestions)
                                .executes(context ->
                                        giveBags(context, 1))
                                .then(Commands.argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int count = IntegerArgumentType.getInteger(context, "amount");
                                            return giveBags(context, count);
                                        })
                                )
                        )
                )
        );

        // list
        builder.then(Commands.literal("list")
                .executes(TreasureBagsCommand::listBagTypes)
        );

        dispatcher.register(builder);
    }

    private static int giveBags(CommandContext<CommandSourceStack> context, int bagCount) throws CommandSyntaxException {
        ResourceLocation bagTypeId = ResourceLocationArgument.getId(context, "bagType");
        IBagType bagType = BagTypeManager.getValue(bagTypeId);
        if (bagType == null) {
            context.getSource().sendFailure(translate("give.invalid", bagTypeId.toString()));
            return 0;
        }

        for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
            ItemStack stack = TbItems.TREASURE_BAG.get().stackOfType(bagType, bagCount);
            PlayerUtils.giveItem(player, stack);
            context.getSource().sendSuccess(translate("give.success", bagCount, bagType.getCustomName(), player.getScoreboardName()), true);
        }

        return 1;
    }

    private static int listBagTypes(CommandContext<CommandSourceStack> context) {
        String str = BagTypeManager.getValues().stream()
                .map(type -> type.getId().toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendSuccess(Component.literal(str), true);
        return 1;
    }

    private static Component translate(String key, Object... args) {
        return Component.translatable("command.treasurebags." + key, args);
    }
}
