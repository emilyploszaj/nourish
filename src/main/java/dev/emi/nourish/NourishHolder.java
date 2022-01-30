package dev.emi.nourish;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

public class NourishHolder implements EntityComponentInitializer {
    public static final ComponentKey<NourishComponent> NOURISH = ComponentRegistry.getOrCreate(new Identifier("nourish:nourish"), NourishComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(NOURISH, PlayerNourishComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
