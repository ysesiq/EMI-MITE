package emi.dev.emi.emi.data;

import emi.dev.emi.emi.api.stack.EmiIngredient;

import java.util.List;

public record IndexStackData(List<Added> added, List<EmiIngredient> removed) {
	
	public static record Added(EmiIngredient added, EmiIngredient after) {
	}
}
