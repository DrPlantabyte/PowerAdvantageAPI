package cyano.poweradvantage.item;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
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

	public ItemRockHammer createTool(int toolLevel){
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
		// TODO: replace drop with crushed recipe
		return super.onBlockDestroyed(tool, world, target, coord, player);
	}
	protected boolean isCrushableBlock(Block b){
		// TODO: implementation
		return false;
	}
}
