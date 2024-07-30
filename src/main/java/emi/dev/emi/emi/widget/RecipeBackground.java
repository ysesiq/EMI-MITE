package emi.dev.emi.emi.widget;

import emi.dev.emi.emi.EmiRenderHelper;
import emi.dev.emi.emi.api.widget.Widget;
import emi.dev.emi.emi.runtime.EmiDrawContext;
import emi.dev.emi.emi.screen.Bounds;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import net.minecraft.ResourceLocation;

public class RecipeBackground extends Widget {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/background.png");
	private final int x, y, width, height;
	
	public RecipeBackground(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public Bounds getBounds() {
		return new Bounds(0, 0, 0, 0);
	}
	
	@Override
	public void render(DrawContext raw, int mouseX, int mouseY, float delta) {
		EmiDrawContext context = EmiDrawContext.wrap(raw);
		EmiRenderHelper.drawNinePatch(context, TEXTURE, x, y, width, height, 27, 0, 4, 1);
	}
}
