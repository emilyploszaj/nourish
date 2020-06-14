package dev.emi.nourish.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.nourish.client.NourishScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.container.PlayerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerContainer> implements RecipeBookProvider {
	private static final Identifier GUI_TEX = new Identifier("nourish", "textures/gui/gui.png");
	private TexturedButtonWidget nourishWidget;

	public InventoryScreenMixin(PlayerContainer container, PlayerInventory inventory, Text text) {
		super(container, inventory, text);
	}

	@Inject(at = @At("TAIL"), method = "init")
	public void init(CallbackInfo info) {
		nourishWidget = new TexturedButtonWidget(this.x + this.containerWidth - 9 - 5, this.y + 5, 9, 9, 0, 20, 9, GUI_TEX, (widget) -> {
			MinecraftClient.getInstance().openScreen(new NourishScreen(true));
		});
		this.addButton(nourishWidget);
	}

	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(CallbackInfo info) {
		nourishWidget.setPos(this.x + this.containerWidth - 9 - 5, this.y + 5);// :tiny_potato:
	}
}