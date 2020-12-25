package dev.emi.nourish;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.groups.NourishGroups;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import static com.mojang.brigadier.arguments.FloatArgumentType.floatArg;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.List;

public class NourishMain implements ModInitializer {
	public static final ComponentType<NourishComponent> NOURISH = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("nourish:nourish"), NourishComponent.class);

	private final static SuggestionProvider<ServerCommandSource> NUTRIENT_SUGGESTIONS = (context, builder) -> {
		List<String> nutrients = Lists.newArrayList();
		for (NourishGroup group: NourishGroups.groups) {
			nutrients.add(group.name);
		}
		return CommandSource.suggestMatching(nutrients, builder);
	};

	@Override
	public void onInitialize() {
		EntityComponentCallback.event(PlayerEntity.class).register((player, components) -> components.put(NOURISH, new PlayerNourishComponent(player)));
		NourishGroups.init();
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(literal("nourish")
				.then(
					literal("get")
					.then(
						argument("group", word())
						.suggests(NUTRIENT_SUGGESTIONS)
						.executes(context -> {
							NourishGroup group = NourishGroups.byName.get(context.getArgument("group", String.class));
							if (group == null) {
								throw new SimpleCommandExceptionType(new TranslatableText("nourish.command.invalid_group")).create();
							}
							NOURISH.maybeGet(context.getSource().getPlayer()).ifPresent(component -> {
								context.getSource().sendFeedback(new TranslatableText("nourish.command.value", group.name, component.getValue(group)), false);
							});
							return Command.SINGLE_SUCCESS;
						})
					)
				)
				.then(
					literal("set")
					.then(
						argument("group", word())
						.suggests(NUTRIENT_SUGGESTIONS)
						.then(
							argument("amount", floatArg(0.0F, 1.0F))
							.executes(context -> {
								NourishGroup group = NourishGroups.byName.get(context.getArgument("group", String.class));
								final float value = context.getArgument("amount", Float.class);
								if (group == null) {
									throw new SimpleCommandExceptionType(new TranslatableText("nourish.command.invalid_group")).create();
								}
								NOURISH.maybeGet(context.getSource().getPlayer()).ifPresent(component -> {
									component.setValue(group, value);
									context.getSource().sendFeedback(new TranslatableText("nourish.command.set", group.name, value), false);
								});
								return Command.SINGLE_SUCCESS;
							})
						)
					)
				)
			);
		});
	}
}