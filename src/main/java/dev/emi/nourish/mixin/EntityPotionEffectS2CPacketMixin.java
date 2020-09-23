package dev.emi.nourish.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.nourish.effects.NourishStatusEffectInstance;
import dev.emi.nourish.wrapper.EntityPotionEffectS2CPacketWrapper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;

@Mixin(EntityStatusEffectS2CPacket.class)
public class EntityPotionEffectS2CPacketMixin implements EntityPotionEffectS2CPacketWrapper {
	@Shadow
	private byte flags;
	
	@Inject(at = @At("RETURN"), method = "<init>(ILnet/minecraft/entity/effect/StatusEffectInstance;)V")
	public void init(int i, StatusEffectInstance inst, CallbackInfo info) {
		if (inst instanceof NourishStatusEffectInstance) {
			flags |= 32;// Skip a couple, why not
		}
	}

	@Override
	public boolean getNourishFlag() {
		return (flags &= 32) == 32;
	}
}