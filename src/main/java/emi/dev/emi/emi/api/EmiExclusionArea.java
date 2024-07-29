package emi.dev.emi.emi.api;

import emi.dev.emi.emi.screen.Bounds;
import net.minecraft.GuiScreen;

import java.util.function.Consumer;

public interface EmiExclusionArea<T extends GuiScreen> {
	
	void addExclusionArea(T screen, Consumer<Bounds> consumer);
}
