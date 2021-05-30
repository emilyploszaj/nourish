package dev.emi.nourish.mixin;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.nourish.NourishMain;
import dev.emi.nourish.client.NourishScreen;
import dev.emi.nourish.effects.NourishEffect;
import dev.emi.nourish.effects.NourishEffectCondition;
import dev.emi.nourish.effects.NourishStatusEffectInstance;
import dev.emi.nourish.groups.NourishGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {
	private static final Identifier GUI_TEX = new Identifier("nourish", "textures/gui/gui.png");
	private TexturedButtonWidget nourishWidget;

	@Shadow @Final
	private RecipeBookWidget recipeBook;

	public InventoryScreenMixin(PlayerScreenHandler container, PlayerInventory inventory, Text text) {
		super(container, inventory, text);
	}

	@Inject(at = @At("TAIL"), method = "init")
	public void init(CallbackInfo info) {
		nourishWidget = new TexturedButtonWidget(this.x + this.backgroundWidth - 9 - 5, this.y + 5, 9, 9, 0, 20, 9, GUI_TEX, (widget) -> {
			MinecraftClient.getInstance().openScreen(new NourishScreen(true));
		});
		this.addButton(nourishWidget);
	}

	@Inject(at = @At("TAIL"), method = "render")
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if (this.client.player.inventory.getCursorStack().isEmpty() && (this.focusedSlot == null || !this.focusedSlot.hasStack())) {
			if (!recipeBook.isOpen()) {
				if (mouseX < this.x - 8 && mouseX > this.x - 122 && mouseY > this.y && mouseY < this.height - this.y) {
					Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
					RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
					int effectHeight = 33;
					if (collection.size() > 5) {
						effectHeight = 132 / (collection.size() - 1);
					}
					int e = (mouseY - this.y) / effectHeight;
					int r = (mouseY - this.y) % effectHeight;
					List<StatusEffectInstance> effects = Ordering.natural().sortedCopy(collection);
					if (e < effects.size() && r > 2 && r < 29) {
						StatusEffectInstance effect = effects.get(e);
						if (effect instanceof NourishStatusEffectInstance) {
							NourishMain.NOURISH.maybeGet(this.client.player).ifPresent(comp -> {
								List<NourishEffect> nourishEffects = Lists.newArrayList();
								for (NourishEffect eff: comp.getProfile().effects) {
									if (eff.test(comp)) {
										for (Pair<StatusEffect, Integer> status : eff.status_effects) {
											if (status.getLeft() == effect.getEffectType() && status.getRight() == effect.getAmplifier()) {
												nourishEffects.add(eff);
											}
										}
									}
								}
								if (nourishEffects.size() > 0) {
									List<Text> lines = Lists.newArrayList();
									boolean first = true;
									for (NourishEffect eff: nourishEffects) {
										if (!first) {
											lines.add(new LiteralText(""));
										}
										first = false;
										lines.add(new TranslatableText("nourish.effect.caused"));
										for (NourishEffectCondition condition: eff.conditions) {
											List<String> groups = Lists.newArrayList();
											for (NourishGroup group: condition.groups) {
												groups.add(new TranslatableText("nourish.group." + group.name).getString());
											}
											Text text = new LiteralText(String.join(", ", groups));
											String root = "nourish.effect.cause.multiple";
											if (groups.size() == 1) {
												root = "nourish.effect.cause.single";
											}
											if (condition.above != -1.0F) {
												if (condition.below != 2.0F) {
													lines.add(new TranslatableText(root + ".above_and_below", text,
														(int) (condition.above * 100), (int) (condition.below * 100)));
												} else {
													lines.add(new TranslatableText(root + ".above", text, (int) (condition.above * 100)));
												}
											} else {
												lines.add(new TranslatableText(root + ".below", text, (int) (condition.below * 100)));
											}
										}
									}
									this.renderTooltip(matrices, lines, mouseX, mouseY);
								}
							});
						}
					}
				}
			}
		}
	}

	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(CallbackInfo info) {
		nourishWidget.setPos(this.x + this.backgroundWidth - 9 - 5, this.y + 5);// :tiny_potato:
	}
}