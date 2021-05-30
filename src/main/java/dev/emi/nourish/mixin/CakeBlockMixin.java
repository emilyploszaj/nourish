package dev.emi.nourish.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.nourish.NourishMain;
import dev.emi.nourish.groups.NourishGroup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin extends Block {
	
	public CakeBlockMixin(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("RETURN"), method = "tryEat")
	private void tryEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResult> info) {
		if (info.getReturnValue() == ActionResult.SUCCESS) {
			for (NourishGroup group: NourishMain.NOURISH.get(player).getProfile().groups) {
				Tag<Item> tag = player.world.getTagManager().getItems().getTagOrEmpty(group.identifier);
				if (tag.contains(new ItemStack((Block) this).getItem())) {
					NourishMain.NOURISH.get(player).consume(group, 2 + 0.1F);
					NourishMain.NOURISH.get(player).sync();
				}
			}
		}
	}
}