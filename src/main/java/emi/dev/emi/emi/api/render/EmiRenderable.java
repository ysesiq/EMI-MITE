package emi.dev.emi.emi.api.render;

import emi.shims.java.net.minecraft.client.gui.DrawContext;

/**
 * Provides a method to render something at a position
 */
public interface EmiRenderable {
	
	void render(DrawContext draw, int x, int y, float delta);
}
