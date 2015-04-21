package cyano.poweradvantage.api.fluid;

import java.util.Set;

import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import cyano.poweradvantage.api.ConduitBlock;
import cyano.poweradvantage.api.ConduitType;
import cyano.poweradvantage.init.Fluids;

public abstract class FluidConduitBlock extends ConduitBlock{
	
	// treat fluids as just another type of power, with 1 exception:
	// There is a generalized ConduitType (Fluids.fluidConduit_general) used for connecting blocks 
	// and specific ConduitTypes for each type of fluid. Therefore, the "power type" of a fluid 
	// block is the general type but fluid "power sources" ask for requests using the specific type 
	// of fluid that they have available

	
	protected FluidConduitBlock(Material mat) {
		super(mat);
	}

	
	@Override
	public boolean canAcceptType(ConduitType type, EnumFacing blockFace) {
		return canAcceptType(type);
	}

	@Override
	public boolean canAcceptType(ConduitType type) {
		return ConduitType.areSameType(type, Fluids.fluidConduit_general) ;
	}

	@Override
	public ConduitType getType() {
		return Fluids.fluidConduit_general;
	}


}
