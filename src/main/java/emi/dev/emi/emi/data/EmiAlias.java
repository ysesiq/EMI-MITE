package emi.dev.emi.emi.data;

import emi.dev.emi.emi.api.stack.EmiIngredient;

import java.util.List;

public record EmiAlias(List<EmiIngredient> stacks, List<String> keys) {
}
