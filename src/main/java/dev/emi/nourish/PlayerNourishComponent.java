package dev.emi.nourish;

import java.util.HashMap;
import java.util.Map;

import dev.emi.nourish.effects.NourishEffect;
import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.profile.NourishProfile;
import dev.emi.nourish.profile.NourishProfiles;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.component.extension.CopyableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class PlayerNourishComponent implements NourishComponent, CopyableComponent<PlayerNourishComponent> {
	private PlayerEntity player;
	private NourishProfile profile;
	private Map<NourishGroup, Float> nourishment = new HashMap<NourishGroup, Float>();

	public PlayerNourishComponent(PlayerEntity player) {
		this.player = player;
		profile = NourishProfiles.getProfile("");
		for (NourishGroup group: profile.groups) {
			nourishment.put(group, group.getDefaultValue());
		}
	}

	@Override
	public void copyFrom(PlayerNourishComponent other) {
		setProfile(other.getProfile());
	}

	@Override
	public NourishProfile getProfile() {
		return profile;
	}

	@Override
	public void setProfile(NourishProfile profile) {
		if (this.profile != profile) {
			this.profile = profile;
			nourishment.clear();
			for (NourishGroup group: profile.groups) {
				nourishment.put(group, group.getDefaultValue());
			}
			sync();
		}
	}

	@Override
	public void consumeFood(ItemStack stack) {
		consumeFood(stack, true);
	}
	private void consumeFood(ItemStack stack, boolean s) {
		Identifier id = Registry.ITEM.getId(stack.getItem());
		if (id.toString().equals("sandwichable:sandwich")) {
			DefaultedList<ItemStack> foods = DefaultedList.ofSize(128, ItemStack.EMPTY);
			Inventories.fromTag(stack.getSubTag("BlockEntityTag"), foods);
			for (ItemStack food: foods) {
				if (food.isEmpty()) break;
				consumeFood(food, false);
			}
		} else {
			for (NourishGroup group: profile.groups) {
				Tag<Item> tag = player.world.getTagManager().getItems().getTagOrEmpty(group.identifier);
				if (tag.contains(stack.getItem())) {
					FoodComponent comp = stack.getItem().getFoodComponent();
					consume(group, comp.getHunger() + comp.getSaturationModifier());
				}
			}
		}
		if (s) {
			sync();
		}
	}

	public void consume(NourishGroup group, float val) {
		float f = nourishment.get(group);
		f += Math.sqrt(val) / 24f * group.getMultiplier();
		if (f > 1) f = 1;
		nourishment.put(group, f);

	}

	@Override
	public void decay() {
		for (NourishGroup group: profile.groups) {
			float f = nourishment.get(group);
			nourishment.put(group, f);
			f -= group.getDecay() / 3000f;
			if (f < 0) f = 0f;
			nourishment.put(group, f);
		}
		for (NourishEffect eff: profile.effects) {
			if (eff.test(this)) {
				eff.apply(player);
			}
		}
		sync();
	}

	@Override
	public void exhaust() {
		for (NourishGroup group: profile.groups) {
			float f = nourishment.get(group);
			nourishment.put(group, f);
			f -= group.getDecay() / 1000f;
			if (f < 0) f = 0f;
			nourishment.put(group, f);
		}
		sync();
	}

	@Override
	public float getValue(NourishGroup group) {
		return nourishment.get(group);
	}

	@Override
	public void setValue(NourishGroup group, float val) {
		nourishment.put(group, val);
	}

	@Override
	public void fromTag(CompoundTag tag) {
		if (tag.contains("Profile")) {
			setProfile(NourishProfiles.getProfile(tag.get("Profile").asString()));
		}
		for (Map.Entry<NourishGroup, Float> entry: nourishment.entrySet()) {
			if (tag.contains(entry.getKey().name)) {
				entry.setValue(tag.getFloat(entry.getKey().name));
			}
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putString("Profile", profile.name);
		for (Map.Entry<NourishGroup, Float> entry: nourishment.entrySet()) {
			tag.putFloat(entry.getKey().name, entry.getValue());
		}
		return tag;
	}

	@Override
	public ComponentType<?> getComponentType() {
		return NourishMain.NOURISH;
	}

	@Override
	public Entity getEntity() {
		return player;
	}
	
}