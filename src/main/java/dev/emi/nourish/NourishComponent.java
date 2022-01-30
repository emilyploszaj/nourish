package dev.emi.nourish;

import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.profile.NourishProfile;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.item.ItemStack;

public interface NourishComponent extends AutoSyncedComponent {

	public NourishProfile getProfile();

	public void setProfile(NourishProfile profile);
	
	public void consumeFood(ItemStack stack);
	
	public void decay();
	
	public void exhaust();
	
	public float getValue(NourishGroup group);

	public void setValue(NourishGroup group, float val);

	public void consume(NourishGroup group, float val);
}