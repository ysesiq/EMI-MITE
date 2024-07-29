package emi.dev.emi.emi.recipe.special;

import com.google.common.collect.Lists;
import emi.dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.GeneratedSlotWidget;
import emi.dev.emi.emi.api.widget.SlotWidget;
import emi.shims.java.net.minecraft.item.DyeItem;
import emi.shims.java.net.minecraft.util.DyeColor;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.NBTTagCompound;
import net.minecraft.ResourceLocation;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EmiFireworkStarRecipe extends EmiPatternCraftingRecipe {
	private static final List<DyeItem> DYES = Stream.of(DyeColor.values()).map(DyeItem::byColor).collect(Collectors.toList());
	
	private static final List<ItemStack> SHAPES =
			List.of(new ItemStack(Item.fireballCharge), new ItemStack(Item.feather), new ItemStack(Item.goldNugget), new ItemStack(Item.skull, 1, 0),
					new ItemStack(Item.skull, 1, 1), new ItemStack(Item.skull, 1, 2), new ItemStack(Item.skull, 1, 3), new ItemStack(Item.skull, 1, 4));
	
	private static final List<Item> EFFECTS = List.of(Item.diamond, Item.glowstone);
	
	public EmiFireworkStarRecipe(ResourceLocation id) {
		super(List.of(EmiIngredient.of(DYES.stream().map(i -> (EmiIngredient) EmiStack.of(i)).collect(Collectors.toList())),
						EmiIngredient.of(SHAPES.stream().map(i -> (EmiIngredient) EmiStack.of(i)).collect(Collectors.toList())),
						EmiIngredient.of(EFFECTS.stream().map(i -> (EmiIngredient) EmiStack.of(i)).collect(Collectors.toList())), EmiStack.of(Item.gunpowder)),
				EmiStack.of(Item.fireworkCharge), id);
	}
	
	@Override
	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new SlotWidget(EmiStack.of(Item.gunpowder), x, y);
		}
		else {
			final int s = slot - 1;
			return new GeneratedSlotWidget(r -> {
				List<ItemStack> items = getItems(r);
				if (s < items.size()) {
					return EmiStack.of(items.get(s));
				}
				return EmiStack.EMPTY;
			}, unique, x, y);
		}
	}
	
	@Override
	public SlotWidget getOutputWidget(int x, int y) {
		return new GeneratedSlotWidget(this::getFireworkStar, unique, x, y);
	}
	
	private List<DyeItem> getDyes(Random random, int max) {
		List<DyeItem> dyes = Lists.newArrayList();
		int amount = 1 + random.nextInt(max);
		for (int i = 0; i < amount; i++) {
			dyes.add(DYES.get(random.nextInt(DYES.size())));
		}
		return dyes;
	}
	
	private List<ItemStack> getItems(Random random) {
		List<ItemStack> items = Lists.newArrayList();
		int amount = random.nextInt(4);
		if (amount < 2) {
			items.add(new ItemStack(EFFECTS.get(amount)));
		}
		else if (amount == 2) {
			items.add(new ItemStack(EFFECTS.get(0)));
			items.add(new ItemStack(EFFECTS.get(1)));
		}
		amount = random.nextInt(10);
		if (amount < 8) {
			items.add(SHAPES.get(amount));
		}
		
		getDyes(random, 8 - items.size()).stream().map(DyeItem::toStack).forEach(items::add);
		
		return items;
	}
	
	private EmiStack getFireworkStar(Random random) {
		ItemStack stack = new ItemStack(Item.fireworkCharge);
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagCompound explosion = new NBTTagCompound();
		boolean hasShape = false;
		
		List<ItemStack> items = getItems(random);
		byte smallBall = 0;
		byte largeBall = 1;
		byte star = 2;
		byte creeper = 3;
		byte burst = 4;
		List<Integer> colors = Lists.newArrayList();
		
		for (ItemStack item : items) {
			if (Item.glowstone.equals(item.getItem())) {
				explosion.setByte("Flicker", largeBall);
			}
			else if (Item.diamond.equals(item.getItem())) {
				explosion.setByte("Trail", largeBall);
			}
			else if (Item.fireballCharge.equals(item.getItem())) {
				explosion.setByte("Type", largeBall);
				hasShape = true;
			}
			else if (Item.goldNugget.equals(item.getItem())) {
				explosion.setByte("Type", star);
				hasShape = true;
			}
			else if (Item.feather.equals(item.getItem())) {
				explosion.setByte("Type", burst);
				hasShape = true;
			}
			else if (Item.skull.equals(item.getItem())) {
				explosion.setByte("Type", creeper);
				hasShape = true;
			}
			else {
				colors.add(DyeColor.values()[1].getFireworkColor());
				colors.add(DyeColor.values()[item.getItemDamage()].getFireworkColor());
			}
		}
		if (!hasShape) {
			explosion.setByte("Type", smallBall);
		}
		
		explosion.setIntArray("Colors", colors.stream().mapToInt(Integer::intValue).toArray());
		tag.setTag("Explosion", explosion);
		stack.setTagCompound(tag);
		return EmiStack.of(stack);
	}
}

