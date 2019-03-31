package net.silentchaos512.loot.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.silentchaos512.lib.util.PlayerUtils;
import net.silentchaos512.loot.init.ModItems;
import net.silentchaos512.loot.lib.BagTypeManager;
import net.silentchaos512.loot.lib.IBagType;

public class GiveTreasureBagCommand {
    private static final SuggestionProvider<CommandSource> bagTypeSuggestions = (ctx, builder) ->
            ISuggestionProvider.func_212476_a(BagTypeManager.getValues().stream().map(IBagType::getId), builder);

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("tb_give")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("players", EntityArgument.players()).then(
                        Commands.argument("bagType", ResourceLocationArgument.resourceLocation())
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
    }

    private static int giveBags(CommandContext<CommandSource> context, int bagCount) throws CommandSyntaxException {
        ResourceLocation bagTypeId = ResourceLocationArgument.getResourceLocation(context, "bagType");
        IBagType bagType = BagTypeManager.getValue(bagTypeId);
        if (bagType == null) {
            context.getSource().sendErrorMessage(translate("invalid", bagTypeId.toString()));
            return 0;
        }

        for (EntityPlayerMP player : EntityArgument.getPlayers(context, "players")) {
            ItemStack stack = ModItems.treasureBag.stackOfType(bagType, bagCount);
            PlayerUtils.giveItem(player, stack);
            context.getSource().sendFeedback(translate("success", bagCount, bagType.getCustomName(), player.getScoreboardName()), true);
        }

        return 1;
    }

    private static ITextComponent translate(String key, Object... args) {
        return new TextComponentTranslation("command.treasurebags.give." + key, args);
    }
}
