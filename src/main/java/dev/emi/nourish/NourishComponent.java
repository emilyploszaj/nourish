package dev.emi.nourish;

import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.profile.NourishProfile;
import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.item.ItemStack;

public interface NourishComponent extends EntitySyncedComponent {

	public NourishProfile getProfile();

	public void setProfile(NourishProfile profile);
	
	public void consumeFood(ItemStack stack);
	
	public void decay();
	
	public void exhaust();
	
	public float getValue(NourishGroup group);

	public void setValue(NourishGroup group, float val);

	public void consume(NourishGroup group, float val);
}