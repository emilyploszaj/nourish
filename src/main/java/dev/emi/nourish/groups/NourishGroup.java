package dev.emi.nourish.groups;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class NourishGroup {
	public transient Identifier identifier;
	public transient Tag<Item> tag;
	public String name;
	public String color;
	public float default_value = 0.8f;
	public float decay = 0.2f;
	public float multiplier = 1f;
	public boolean secondary = false;

	public NourishGroup(Identifier id) {
		identifier = id;
	}

	public float getDefaultValue() {
		return default_value;
	}

	public float getDecay() {
		return decay;
	}

	public float getMultiplier() {
		return multiplier;
	}

	public int getColor() {
		int i = Integer.parseInt(color, 16);
		return i % 0x1000000;
	}
}