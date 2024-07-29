package emi.dev.emi.emi.runtime;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.EmiRenderHelper;
import emi.dev.emi.emi.api.EmiApi;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.config.EmiConfig;
import emi.dev.emi.emi.config.HelpLevel;
import emi.dev.emi.emi.registry.EmiRecipeFiller;
import emi.dev.emi.emi.screen.StackBatcher.Batchable;
import emi.dev.emi.emi.screen.tooltip.RecipeTooltipComponent;
import emi.shims.java.net.minecraft.client.gui.DrawContext;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import emi.shims.java.net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmiFavorite implements EmiIngredient, Batchable {
	protected final EmiIngredient stack;
	protected final @Nullable EmiRecipe recipe;

	public EmiFavorite(EmiIngredient stack, @Nullable EmiRecipe recipe) {
		this.stack = stack;
		this.recipe = recipe;
	}

	public EmiIngredient getStack() {
		return stack;
	}

	@Override
	public EmiIngredient copy() {
		return new EmiFavorite(stack, recipe);
	}

	@Override
	public long getAmount() {
		return stack.getAmount();
	}

	@Override
	public EmiIngredient setAmount(long amount) {
		return this;
	}

	@Override
	public float getChance() {
		return 1;
	}

	@Override
	public EmiIngredient setChance(float chance) {
		return this;
	}

	public EmiRecipe getRecipe() {
		return recipe;
	}

	@Override
	public List<EmiStack> getEmiStacks() {
		return stack.getEmiStacks();
	}

	@Override
	public void render(DrawContext raw, int x, int y, float delta, int flags) {
		EmiDrawContext context = EmiDrawContext.wrap(raw);
		if (recipe != null) {
			flags |= EmiIngredient.RENDER_AMOUNT;
		}
		stack.render(context.raw(), x, y, delta, flags);
		if ((flags & EmiIngredient.RENDER_INGREDIENT) > 0 && recipe != null) {
			EmiRenderHelper.renderRecipeFavorite(stack, context, x, y);
		}
	}

	@Override
	public List<TooltipComponent> getTooltip() {
		List<TooltipComponent> list = Lists.newArrayList();
		list.addAll(stack.getTooltip());
		if (recipe != null) {
			list.add(new RecipeTooltipComponent(recipe, true));
		}
		return list;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof EmiIngredient ingredient && EmiIngredient.areEqual(this, ingredient);
	}

	@Override
	public boolean isSideLit() {
		return stack instanceof Batchable b && b.isSideLit();
	}

	@Override
	public boolean isUnbatchable() {
		return !(stack instanceof Batchable b) || b.isUnbatchable();
	}

	@Override
	public void setUnbatchable() {
		if (stack instanceof Batchable b) {
			b.setUnbatchable();
		}
	}

	@Override
	public void renderForBatch(DrawContext raw, int x, int y, int z, float delta) {
		if (stack instanceof Batchable b) {
			b.renderForBatch(raw, x, y, z, delta);
		}
	}

	public static class Craftable extends EmiFavorite {

		public Craftable(EmiRecipe recipe) {
			super(recipe.getOutputs().isEmpty() ? EmiStack.EMPTY : recipe.getOutputs().get(0), recipe);
		}

		@Override
		public void render(DrawContext raw, int x, int y, float delta, int flags) {
			super.render(raw, x, y, delta, flags & (~EmiIngredient.RENDER_INGREDIENT));
		}
	}

	public static class Synthetic extends EmiFavorite {
		public final long batches;
		public final long amount;
		public final int state;
		public long total;

		public Synthetic(EmiRecipe recipe, long batches, long amount, int state) {
			super(recipe.getOutputs().get(0), recipe);
			this.batches = batches;
			this.amount = amount;
			this.state = state;
		}

		public Synthetic(EmiIngredient ingredient, long needed, long total) {
			super(ingredient, null);
			this.batches = needed;
			this.amount = needed;
			this.total = total;
			this.state = -1;
		}

		@Override
		public void render(DrawContext raw, int x, int y, float delta, int flags) {
			EmiDrawContext context = EmiDrawContext.wrap(raw);
			int color = 0xff2200;
			if (state == 1) {
				color = 0xaa00ff;
			} else if (state == 2) {
				color = 0x00dddd;
			} else if (state == -1) {
				color = 0xea842a;
			}
			context.fill(x - 1, y - 1, 18, 18, 0x44000000 | color);
			stack.render(context.raw(), x, y, delta, flags & (~EmiIngredient.RENDER_AMOUNT));
			if (recipe != null) {
				EmiRenderHelper.renderAmount(context, x, y, EmiPort.literal("" + amount));
			} else {
				EmiRenderHelper.renderAmount(context, x, y, EmiRenderHelper.getAmountText(stack, amount));
			}
		}

		@Override
		public List<TooltipComponent> getTooltip() {
			List<TooltipComponent> list = Lists.newArrayList();
			list.addAll(super.getTooltip());
			if (state == -1) {
				return list;
			}
			
			Text craftKey = null;

			if (EmiConfig.helpLevel.has(HelpLevel.NORMAL) && EmiRecipeFiller.getFirstValidHandler(recipe, EmiApi.getHandledScreen()) != null) {
				if (EmiConfig.craftAllToInventory.isBound()) {
					craftKey = EmiConfig.craftAllToInventory.getBindText();
				} else if (EmiConfig.craftAll.isBound()) {
					craftKey = EmiConfig.craftAll.getBindText();
				}
			}
			if (state == 0) {
				list.add(TooltipComponent.of(EmiPort.translatable("tooltip.emi.synfav.uncraftable").asOrderedText()));
			} else if (state == 1) {
				list.add(TooltipComponent.of(EmiPort.translatable("tooltip.emi.synfav.partially_craftable").asOrderedText()));
				if (craftKey != null) {
					list.add(TooltipComponent.of(EmiPort.translatable("tooltip.emi.synfav.craft_some", craftKey).asOrderedText()));
				}
			} else if (state == 2) {
				list.add(TooltipComponent.of(EmiPort.translatable("tooltip.emi.synfav.fully_craftable", batches).asOrderedText()));
				if (craftKey != null) {
					list.add(TooltipComponent.of(EmiPort.translatable("tooltip.emi.synfav.craft_all", craftKey, batches).asOrderedText()));
				}
			}
			return list;
		}

		@Override
		public boolean isUnbatchable() {
			return true;
		}
	}
}
