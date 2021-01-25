package dev.emi.nourish.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.nourish.NourishComponent;
import dev.emi.nourish.NourishMain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
	public float exhaustion;
	public int ticks;
	
	@Inject(at = @At("HEAD"), method = "eatFood")
	public void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> info) {
		if (!world.isClient && stack.isFood()) {
			NourishComponent comp = NourishMain.NOURISH.get((PlayerEntity) (Object) this);
			comp.consumeFood(stack);
		}
	}

	@Inject(at = @At("HEAD"), method = "addExhaustion")
	public void addExhaustion(float exhaustion, CallbackInfo info) {
		this.exhaustion += exhaustion;
		if (this.exhaustion > 1.0f) {
			this.exhaustion -= 1.0f;
			PlayerEntity player = (PlayerEntity) (Object) this;
			NourishComponent comp = NourishMain.NOURISH.get(player);
			comp.exhaust();
		}
	}

	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(CallbackInfo info) {
		if (ticks >= 20) {
			ticks = 0;
			PlayerEntity player = (PlayerEntity) (Object) this;
			if (!player.world.isClient && !player.isCreative()) {
				NourishComponent comp = NourishMain.NOURISH.get(player);
				comp.decay();
			}
		}
		ticks++;
	}
}