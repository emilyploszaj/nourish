package dev.emi.nourish.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.emi.nourish.effects.NourishStatusEffectInstance;
import dev.emi.nourish.wrapper.EntityPotionEffectS2CPacketWrapper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.EntityPotionEffectS2CPacket;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Redirect(method = "onEntityPotionEffect", at = @At(value = "NEW", args = "class=net/minecraft/entity/effect/StatusEffectInstance"))
	public StatusEffectInstance onEntityPotionEffect(StatusEffect effect, int i, int j, boolean a, boolean b, boolean c, EntityPotionEffectS2CPacket packet) {
		if (((EntityPotionEffectS2CPacketWrapper) packet).getNourishFlag()) {
			return new NourishStatusEffectInstance(effect, i, j);
		}
		return new StatusEffectInstance(effect, i, j, a, b, c);
	}
}