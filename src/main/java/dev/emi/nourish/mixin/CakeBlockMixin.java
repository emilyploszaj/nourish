package dev.emi.nourish.mixin;

import dev.emi.nourish.NourishHolder;
import dev.onyxstudios.cca.api.v3.entity.PlayerSyncCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
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
	private static void tryEat(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<ActionResult> info) {
		if (info.getReturnValue() == ActionResult.SUCCESS) {
			for (NourishGroup group: NourishHolder.NOURISH.get(player).getProfile().groups) {
				Tag<Item> tag = player.world.getTagManager().getTag(Registry.ITEM_KEY, group.identifier, identifier -> new RuntimeException("Could not find item tag for " + group.identifier));
				if (tag.contains(state.getBlock().asItem())) {
					NourishHolder.NOURISH.get(player).consume(group, 2 + 0.1F);
					NourishHolder.NOURISH.sync(player);
				}
			}
		}
	}
}