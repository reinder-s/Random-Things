package lumien.randomthings.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;

public class EnchantmentMagnetic extends Enchantment
{
	protected EnchantmentMagnetic(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots)
	{
		super(rarityIn, EnumEnchantmentType.DIGGER, slots);
		this.setName("randomthings.magnetic");
		this.setRegistryName(new ResourceLocation("randomthings", "magnetic"));
	}

	public int getMinEnchantability(int enchantmentLevel)
	{
		return 15;
	}

	public int getMaxEnchantability(int enchantmentLevel)
	{
		return super.getMinEnchantability(enchantmentLevel) + 50;
	}

	public int getMaxLevel()
	{
		return 1;
	}

	public boolean canApplyTogether(Enchantment ench)
	{
		return super.canApplyTogether(ench);
	}
}