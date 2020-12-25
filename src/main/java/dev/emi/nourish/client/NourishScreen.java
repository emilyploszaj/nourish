package dev.emi.nourish.client;

import java.util.List;

import com.google.common.collect.Lists;

import dev.emi.nourish.NourishComponent;
import dev.emi.nourish.NourishMain;
import dev.emi.nourish.groups.NourishGroup;
import dev.emi.nourish.groups.NourishGroups;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class NourishScreen extends Screen {
	private static final Identifier GUI_TEX = new Identifier("nourish", "textures/gui/gui.png");
	private boolean returnToInv;
	private int maxNameLength = 0;
	private int w;
	private int h;
	private int x;
	private int y;	

	public NourishScreen() {
		super(new TranslatableText("nourish.gui.nourishment"));
	}

	public NourishScreen(boolean returnToInv) {
		this();
		this.returnToInv = returnToInv;
	}

	@Override
	public void init(MinecraftClient client, int int_1, int int_2) {
		super.init(client, int_1, int_2);
		for (NourishGroup group: NourishGroups.groups) {
			int l = this.textRenderer.getWidth(new TranslatableText("nourish.group." + group.identifier.getPath()).getString());
			if (l > maxNameLength) {
				maxNameLength = l;
			}
		}
		w = maxNameLength + 120;
		h = 34 + NourishGroups.groups.size() * 20;
		if (NourishGroups.groups.size() > 0 && NourishGroups.groups.get(NourishGroups.groups.size() - 1).secondary) {
			h += 24;
		}
		x = (width - w) / 2 - 2;
		y = (height - h) / 2 - 2;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		client.getTextureManager().bindTexture(GUI_TEX);
		DrawableHelper.drawTexture(matrices, x + 4, y + 4, 4 * w, 3 * h, w - 4, h - 4, 256 * w, 256 * h);
		DrawableHelper.drawTexture(matrices, x + 4, y, 4 * w, 0, w - 4, 4, 256 * w, 256);
		DrawableHelper.drawTexture(matrices, x + 4, y + h, 3 * w, 4, w - 4, 4, 256 * w, 256);
		DrawableHelper.drawTexture(matrices, x, y + 4, 0, 4 * h, 4, h - 4, 256, 256 * h);
		DrawableHelper.drawTexture(matrices, x + w, y + 4, 4, 3 * h, 4, h - 4, 256, 256 * h);
		this.drawTexture(matrices, x, y, 0, 0, 4, 4);
		this.drawTexture(matrices, x + w, y, 4, 0, 4, 4);
		this.drawTexture(matrices, x, y + h, 0, 4, 4, 4);
		this.drawTexture(matrices, x + w, y + h, 4, 4, 4, 4);
		int yo = 28;
		boolean secondary = false;
		for (NourishGroup group: NourishGroups.groups) {
			if (group.secondary && !secondary) {
				secondary = true;
				TranslatableText t = new TranslatableText("nourish.gui.secondary");
				int sw = this.textRenderer.getWidth(t.getString());
				this.textRenderer.draw(matrices, t.getString(), x + w / 2 - sw / 2, y + yo + 4, 4210752);
				yo += 20;
			}
			int color = group.getColor() | 0xFF000000;
			this.textRenderer.draw(matrices, new TranslatableText("nourish.group." + group.identifier.getPath()).getString(), x + 10, y + yo + 4, 4210752);
			NourishComponent comp = NourishMain.NOURISH.get(client.player);
			client.getTextureManager().bindTexture(GUI_TEX);
			this.drawTexture(matrices, x + maxNameLength + 20, y + yo + 2, 0, 8, 90, 12);
			DrawableHelper.fill(matrices, x + maxNameLength + 21, y + yo + 3, x + maxNameLength + 21 + Math.round(88 * comp.getValue(group)), y + yo + 13, color);
			if (mouseX > x + maxNameLength + 20 && mouseY > y + yo + 2 && mouseX < x + maxNameLength + 108 && mouseY < y + yo + 13) {
				if (group.description) {
					List<Text> lines = Lists.newArrayList();
					lines.add(new TranslatableText("nourish.group.description." + group.identifier.getPath()));
					this.renderTooltip(matrices, lines, mouseX, mouseY);
				}
			}
			yo += 20;
		}
		int tw = this.textRenderer.getWidth(this.title.getString());
		this.textRenderer.draw(matrices, this.title.getString(), (width - tw) / 2, y + 6.0F, 4210752);
		super.render(matrices, mouseX, mouseY, delta);
	}

	public boolean keyPressed(int int_1, int int_2, int int_3) {
		if (client.options.keyInventory.matchesKey(int_1, int_2)) {
			if (returnToInv) {
				client.openScreen(new InventoryScreen(client.player));
			} else {
				this.onClose();
			}
			return true;
		} else {
			return super.keyPressed(int_1, int_2, int_3);
		}
	}

	public void onClose() {
		if (returnToInv) {
			client.openScreen(new InventoryScreen(client.player));
			return;
		}
		super.onClose();
	}

	public boolean isPauseScreen() {
		return false;
	}
}