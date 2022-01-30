package dev.emi.nourish.mixin;

import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.nourish.effects.NourishStatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectInstance;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {

	@Inject(at = @At("RETURN"), method = "writeTypelessNbt")
	private void typelessToTag(NbtCompound tag, CallbackInfo info) {
		if ((Object) this instanceof NourishStatusEffectInstance) {
			tag.putBoolean("Nourish", true);
		}
	}
	
	@Inject(at = @At("RETURN"), method = "fromNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/effect/StatusEffectInstance;", cancellable = true)
	private static void fromTag(NbtCompound tag, CallbackInfoReturnable<StatusEffectInstance> info) {
		if (tag.contains("Nourish") && tag.getBoolean("Nourish")) {
			StatusEffectInstance old = info.getReturnValue();
			NourishStatusEffectInstance inst = new NourishStatusEffectInstance(old.getEffectType(), old.getDuration(), old.getAmplifier());
			info.setReturnValue(inst);
		}
	}
}