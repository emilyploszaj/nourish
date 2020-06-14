package dev.emi.nourish;

import dev.emi.nourish.groups.NourishGroup;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.item.ItemStack;

public interface NourishComponent extends EntitySyncedComponent {
	
	public void consumeFood(ItemStack stack);
	
	public void decay();
	
	public void exhaust();
	
	public float getValue(NourishGroup group);

	public void consume(NourishGroup group, float val);
}