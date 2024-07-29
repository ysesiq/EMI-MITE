package emi.dev.emi.emi.screen.tooltip;

import emi.dev.emi.emi.EmiPort;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EmiTooltip {
	public static final DecimalFormat TEXT_FORMAT = new DecimalFormat("0.##");
	
	public static TooltipComponent chance(String type, float chance) {
		return TooltipComponent.of(
				EmiPort.ordered(EmiPort.translatable("tooltip.emi.chance." + type, TEXT_FORMAT.format(chance * 100)).formatted(Formatting.GOLD)));
	}
	
	public static List<TooltipComponent> splitTranslate(String key) {
		return Arrays.stream(RetroEMI.translate(key).split("/n")).map(s -> TooltipComponent.of(EmiPort.ordered(EmiPort.literal(s))))
				.collect(Collectors.toList());
	}
	
	public static List<TooltipComponent> splitTranslate(String key, Object... objects) {
		return Arrays.stream(RetroEMI.translate(key, objects).split("/n"))
				.map(s -> TooltipComponent.of(EmiPort.ordered(EmiPort.literal(s)))).collect(Collectors.toList());
	}
}
