package dev.emi.nourish;

import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.groups.NourishGroups;
import net.minecraft.item.Item;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class NourishTags {

	public static void init() {
		for (NourishGroup group: NourishGroups.groups) {
			group.tag = register(group.identifier);
		}
	}

	private static Tag<Item> register(Identifier id) {
		return new ItemTags.CachingTag(id);
	}
}