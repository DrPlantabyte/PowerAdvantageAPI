package cyano.poweradvantage.item;

import java.util.HashSet;
import java.util.Set;

import cyano.poweradvantage.registry.CrusherRecipeRegistry;
import cyano.poweradvantage.registry.recipe.ICrusherRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemRockHammer extends net.minecraft.item.ItemTool{

	protected ItemRockHammer(float attackDamage,ToolMaterial material,Set<Block> blockSet) {
		super(attackDamage, material, blockSet);
        this.setCreativeTab(CreativeTabs.tabTools);
	}

	public static ItemRockHammer createTool(int toolLevel){
		final ToolMaterial material;
		switch(toolLevel){
			case 1:
				material = ToolMaterial.STONE;
				break;
			case 2:
				material = ToolMaterial.IRON;
				break;
			case 3:
				material = ToolMaterial.EMERALD; // diamond equivalent
				break;
			default:
				material = ToolMaterial.WOOD;
				break;
		}
		float attackDamage = 1+toolLevel;
		Set<Block> blockSet = new HashSet();
		return new ItemRockHammer(attackDamage, material, blockSet);
	}
	
	@Override
    public float getStrVsBlock(final ItemStack tool, final Block target) {
        return isCrushableBlock(target) ? this.toolMaterial.getEfficiencyOnProperMaterial() : 1.0f;
    }
	
	@Override
    public boolean onBlockDestroyed(final ItemStack tool, final World world, 
    		final Block target, final BlockPos coord, final EntityLivingBase player) {
		IBlockState bs = world.getBlockState(coord);
		ICrusherRecipe recipe = getCrusherRecipe(bs);
		return super.onBlockDestroyed(tool, world, target, coord, player);
		
		// TODO: replace drop with crushed recipe
		
	}
	protected boolean isCrushableBlock(IBlockState block){
		return getCrusherRecipe(block) != null;
	}
	protected boolean isCrushableBlock(Block block){
		return getCrusherRecipe(block) != null;
	}
	
	protected ICrusherRecipe getCrusherRecipe(Block block){
		return getCrusherRecipe(block.getDefaultState());
	}
	
	protected ICrusherRecipe getCrusherRecipe(IBlockState block){
		int meta = block.getBlock().getMetaFromState(block);
		ItemStack is = new ItemStack(block.getBlock(), 1, meta);
		return CrusherRecipeRegistry.getInstance().getRecipeForInputItem(is);
	}
}
