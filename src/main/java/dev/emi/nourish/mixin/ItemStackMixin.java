package dev.emi.nourish.mixin;

import java.util.ArrayList;
import java.util.List;

import dev.emi.nourish.NourishHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.emi.nourish.NourishMain;
import dev.emi.nourish.groups.NourishGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow
	public abstract Item getItem();

	@Inject(at = @At("RETURN"), method = "getTooltip")
	public void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info) {
		if (NourishMain.debugTooltip && getItem().isFood()) {
			info.getReturnValue().add(new LiteralText("NourishFood"));
		}
		if (player == null) return;
		ItemStack stack = (ItemStack) (Object) this;
		Identifier id = Registry.ITEM.getId(stack.getItem());
		List<ItemStack> items = new ArrayList<ItemStack>();
		List<String> groups = new ArrayList<String>();
		if (id.toString().equals("sandwichable:sandwich")) {
			DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);
			Inventories.readNbt(stack.getSubNbt("BlockEntityTag"), foods);
			items.addAll(items);
		} else {
			items.add(stack);
		}
		MinecraftClient client = MinecraftClient.getInstance();
		for (NourishGroup group: NourishHolder.NOURISH.get(client.player).getProfile().groups) {
			for (ItemStack food: items) {
				Tag<Item> tag = player.world.getTagManager().getTag(Registry.ITEM_KEY, group.identifier, (identifier -> new RuntimeException("Tag not found: " + identifier.toString())));
				if (tag.contains(food.getItem())) {
					groups.add(new TranslatableText("nourish.group." + group.name).getString());
					break;
				}
			}
		}
		if (groups.size() > 0) {
			info.getReturnValue().add(new LiteralText(String.join(", ", groups)).formatted(Formatting.GOLD));
		}
	}
}