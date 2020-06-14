package dev.emi.nourish;

import dev.emi.nourish.groups.NourishGroups;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class NourishMain implements ModInitializer {
	public static final ComponentType<NourishComponent> NOURISH = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("nourish:nourish"), NourishComponent.class);

	@Override
	public void onInitialize() {
		EntityComponentCallback.event(PlayerEntity.class).register((player, components) -> components.put(NOURISH, new PlayerNourishComponent(player)));
		NourishGroups.init();
		NourishTags.init();
	}
}