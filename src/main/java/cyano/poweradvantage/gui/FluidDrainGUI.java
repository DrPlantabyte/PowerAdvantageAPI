package cyano.poweradvantage.gui;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLLog;
import cyano.poweradvantage.PowerAdvantage;
import cyano.poweradvantage.api.simple.SimpleMachineGUI;
import cyano.poweradvantage.machines.FluidDrainTileEntity;
import cyano.poweradvantage.math.Integer2D;

public class FluidDrainGUI extends SimpleMachineGUI{

	
	public FluidDrainGUI() {
		super(new ResourceLocation(PowerAdvantage.MODID+":"+"textures/gui/container/fluid_discharge_gui.png"),
				new Integer2D[0]);
	}
	
	@Override 
	public void drawGUIDecorations(Object srcEntity, GUIContainer guiContainer, int x, int y){

		guiContainer.mc.renderEngine.bindTexture(guiDisplayImage);
		// draw indicator
		if(srcEntity instanceof FluidDrainTileEntity){
			FluidStack fs = ((FluidDrainTileEntity)srcEntity).getFluid();
			if(fs != null){
				int h = 50 * fs.amount / ((FluidDrainTileEntity)srcEntity).getFluidCapacity();
				guiContainer.drawRect(x+80, y+59-h,x+80+16, y+69,fs.getFluid().getBlock()
						.getMaterial().getMaterialMapColor().colorValue | 0xFF000000); // TODO: see if this works for custom fluids
			}
		}
		GlStateManager.color(1f,1f,1f,1f); // rest tint
		guiContainer.drawTexturedModalRect(x+77, y+6, 176, 0, 22, 66); // x, y, textureOffsetX, textureOffsetY, width, height)
	}


	private static String objectDump(Object o){
		if(o == null ){
			return "null object";
		}
		if(o.getClass() == null){
			return "null class";
		}
		StringBuilder sb = new StringBuilder();
		try{
			Class c = o.getClass();
			sb.append(c.getName()).append("\n");
			do{
				Field[] fields = c.getDeclaredFields();
				for(Field f : fields){
					f.setAccessible(true);
					sb.append("\t").append(f.getName()).append("=");
					if(f.getType().isArray()){
						sb.append(arrayDump(f.get(o)));
					}else if(f.get(o) instanceof java.util.Map){
						sb.append(mapDump((java.util.Map)f.get(o)));
					}else {
						sb.append(String.valueOf(f.get(o)));
					}
					sb.append("\n");
				}
				Method[] methods = c.getDeclaredMethods();
				for(Method m : methods){
					sb.append("\t").append(m.toGenericString()).append("\n");
				}
				c = c.getSuperclass();
			}while(c != null);
		}catch(IllegalArgumentException | IllegalAccessException ex){
			return "<"+ex.getMessage()+">";
		}
		return sb.toString();
	}
	private static String mapDump(java.util.Map map){
		StringBuilder sb = new StringBuilder();
		for(Object key : map.keySet()){
			sb.append(String.valueOf(key)).append("->");
			if(map.get(key).getClass().isArray()){
				sb.append(arrayDump(map.get(key)));
			} else {
				sb.append(String.valueOf(map.get(key)));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private static String arrayDump(Object array){
		StringBuilder sb = new StringBuilder();
		int size = Array.getLength(array);
		sb.append("[ ");
		boolean addComma = false;
		for(int i = 0; i < size; i++){
			if(addComma)sb.append(", ");
			if(Array.get(array, i).getClass().isArray()){
				sb.append(arrayDump(Array.get(array, i)));
			} else if(Array.get(array, i) instanceof java.util.Map){
				sb.append(mapDump((java.util.Map)Array.get(array, i)));
			}else {
				sb.append(String.valueOf(Array.get(array, i)));
			}
			addComma = true;
		}
		sb.append(" ]");
		return sb.toString();
	}


}
