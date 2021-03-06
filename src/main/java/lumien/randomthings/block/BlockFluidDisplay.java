package lumien.randomthings.block;

import lumien.randomthings.lib.properties.UnlistedBool;
import lumien.randomthings.tileentity.TileEntityFluidDisplay;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

public class BlockFluidDisplay extends BlockContainerBase
{
	public static final FluidProperty FLUID = new FluidProperty();
	public static final UnlistedBool FLOWING = UnlistedBool.create("flowing");

	public BlockFluidDisplay()
	{
		super("fluidDisplay", Material.GLASS);

		this.setSoundType(SoundType.GLASS);
		this.setHardness(0.3F);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileEntityFluidDisplay te = (TileEntityFluidDisplay) worldIn.getTileEntity(pos);

		if (heldItem != null)
		{
			FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(heldItem);
			if (liquid != null)
			{
				if (!worldIn.isRemote)
				{
					te.setFluidName(liquid.getFluid().getName());
					te.syncTE();
				}
				return true;
			}
		}
		else
		{
			if (!worldIn.isRemote)
			{
				te.toggleFlowing();
			}
			return true;
		}
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[] { FLOWING, FLUID });
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileEntityFluidDisplay te = (TileEntityFluidDisplay) worldIn.getTileEntity(pos);

		IExtendedBlockState actualState = (IExtendedBlockState) state;
		if (te == null)
		{
			return actualState.withProperty(FLUID, null).withProperty(FLOWING, false);
		}

		return actualState.withProperty(FLUID, te.getFluid()).withProperty(FLOWING, te.flowing());
	}

	private static class FluidProperty implements IUnlistedProperty<String>
	{
		@Override
		public String getName()
		{
			return "fluid";
		}

		@Override
		public boolean isValid(String value)
		{
			return true;
		}

		@Override
		public Class<String> getType()
		{
			return String.class;
		}

		@Override
		public String valueToString(String value)
		{
			return value;
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityFluidDisplay();
	}
}
