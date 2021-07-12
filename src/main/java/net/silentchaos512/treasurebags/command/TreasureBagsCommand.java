package net.silentchaos512.treasurebags.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.silentchaos512.lib.util.PlayerUtils;
import net.silentchaos512.treasurebags.setup.ModItems;
import net.silentchaos512.treasurebags.lib.BagTypeManager;
import net.silentchaos512.treasurebags.lib.IBagType;

import java.util.stream.Collectors;

public final class TreasureBagsCommand {
    private static final SuggestionProvider<CommandSource> bagTypeSuggestions = (ctx, builder) ->
            ISuggestionProvider.suggestResource(BagTypeManager.getValues().stream().map(IBagType::getId), builder);

    private TreasureBagsCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = Commands.literal("treasurebags")
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

    private static int giveBags(CommandContext<CommandSource> context, int bagCount) throws CommandSyntaxException {
        ResourceLocation bagTypeId = ResourceLocationArgument.getId(context, "bagType");
        IBagType bagType = BagTypeManager.getValue(bagTypeId);
        if (bagType == null) {
            context.getSource().sendFailure(translate("give.invalid", bagTypeId.toString()));
            return 0;
        }

        for (ServerPlayerEntity player : EntityArgument.getPlayers(context, "players")) {
            ItemStack stack = ModItems.TREASURE_BAG.get().stackOfType(bagType, bagCount);
            PlayerUtils.giveItem(player, stack);
            context.getSource().sendSuccess(translate("give.success", bagCount, bagType.getCustomName(), player.getScoreboardName()), true);
        }

        return 1;
    }

    private static int listBagTypes(CommandContext<CommandSource> context) {
        String str = BagTypeManager.getValues().stream()
                .map(type -> type.getId().toString())
                .collect(Collectors.joining(", "));
        context.getSource().sendSuccess(new StringTextComponent(str), true);
        return 1;
    }

    private static ITextComponent translate(String key, Object... args) {
        return new TranslationTextComponent("command.treasurebags." + key, args);
    }
}
