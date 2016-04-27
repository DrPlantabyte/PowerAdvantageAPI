/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cyano.mmu.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author cybergnome
 */
public class PaneMaker {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(true);
		jfc.setDialogTitle("Select block texture files");
		int action = jfc.showOpenDialog(null);
		if(action != JFileChooser.APPROVE_OPTION){
			System.exit(0); // canceled by user
		}
		try{
			File[] files = jfc.getSelectedFiles();
			for(File f : files){
				supportPaneTexture(f.toPath());
			}
		}catch(IOException ex){
			Logger.getLogger(PaneMaker.class.getName()).log(Level.SEVERE,"Error",ex);
			System.exit(ex.getClass().hashCode());
		}
	}
	private static final String KEY_MODID = "MODID";
	private static final String KEY_NAME = "BLOCKNAME";
	private static final String KEY_DIR = "CARDINALS";
	private static final String TEMPLATE_BLOCKSTATE = "{\n" +
"    \"variants\": {\n" +
"        \"east=false,north=false,south=false,west=false\": { \"model\": \"MODID:BLOCKNAME_nsew\" },\n" +
"        \"east=false,north=true,south=false,west=false\": { \"model\": \"MODID:BLOCKNAME_n\" },\n" +
"        \"east=true,north=false,south=false,west=false\": { \"model\": \"MODID:BLOCKNAME_n\", \"y\": 90 },\n" +
"        \"east=false,north=false,south=true,west=false\": { \"model\": \"MODID:BLOCKNAME_n\", \"y\": 180 },\n" +
"        \"east=false,north=false,south=false,west=true\": { \"model\": \"MODID:BLOCKNAME_n\", \"y\": 270 },\n" +
"        \"east=true,north=true,south=false,west=false\": { \"model\": \"MODID:BLOCKNAME_ne\" },\n" +
"        \"east=true,north=false,south=true,west=false\": { \"model\": \"MODID:BLOCKNAME_ne\", \"y\": 90 },\n" +
"        \"east=false,north=false,south=true,west=true\": { \"model\": \"MODID:BLOCKNAME_ne\", \"y\": 180 },\n" +
"        \"east=false,north=true,south=false,west=true\": { \"model\": \"MODID:BLOCKNAME_ne\", \"y\": 270 },\n" +
"        \"east=false,north=true,south=true,west=false\": { \"model\": \"MODID:BLOCKNAME_ns\" },\n" +
"        \"east=true,north=false,south=false,west=true\": { \"model\": \"MODID:BLOCKNAME_ns\", \"y\": 90 },\n" +
"        \"east=true,north=true,south=true,west=false\": { \"model\": \"MODID:BLOCKNAME_nse\" },\n" +
"        \"east=true,north=false,south=true,west=true\": { \"model\": \"MODID:BLOCKNAME_nse\", \"y\": 90 },\n" +
"        \"east=false,north=true,south=true,west=true\": { \"model\": \"MODID:BLOCKNAME_nse\", \"y\": 180 },\n" +
"        \"east=true,north=true,south=false,west=true\": { \"model\": \"MODID:BLOCKNAME_nse\", \"y\": 270 },\n" +
"        \"east=true,north=true,south=true,west=true\": { \"model\": \"MODID:BLOCKNAME_nsew\" }\n" +
"    }\n" +
"}";
	
	private static final String TEMPLATE_ITEMMODEL = "{\n" +
"    \"parent\": \"builtin/generated\",\n" +
"    \"textures\": {\n" +
"        \"layer0\": \"MODID:blocks/BLOCKNAME\"\n" +
"    },\n" +
"    \"display\": {\n" +
"        \"thirdperson\": {\n" +
"            \"rotation\": [ -90, 0, 0 ],\n" +
"            \"translation\": [ 0, 1, -3 ],\n" +
"            \"scale\": [ 0.55, 0.55, 0.55 ]\n" +
"        },\n" +
"        \"firstperson\": {\n" +
"            \"rotation\": [ 0, -135, 25 ],\n" +
"            \"translation\": [ 0, 4, 2 ],\n" +
"            \"scale\": [ 1.7, 1.7, 1.7 ]\n" +
"        }\n" +
"    }\n" +
"}";
	private static final String TEMPLATE_BLOCKMODEL = "{\n" +
"    \"parent\": \"block/pane_CARDINALS\",\n" +
"    \"textures\": {\n" +
"        \"edge\": \"MODID:blocks/BLOCKNAME_top\",\n" +
"        \"pane\": \"MODID:blocks/BLOCKNAME\"\n" +
"    }\n" +
"}";
	private static void supportPaneTexture(Path blockTextureFile) throws IOException{
		Path blockstateDir = Paths.get(blockTextureFile.getParent().getParent().getParent().toString(),"blockstates");
		Path modelsDir = Paths.get(blockTextureFile.getParent().getParent().getParent().toString(),"models","block");
		Path itemModelsDir = Paths.get(blockTextureFile.getParent().getParent().getParent().toString(),"models","item");
		Files.createDirectories(blockstateDir);
		Files.createDirectories(modelsDir);
		Files.createDirectories(itemModelsDir);

		String blockName = blockTextureFile.getFileName().toString();
		blockName = blockName.substring(0, blockName.lastIndexOf("."));
		String modid = blockTextureFile.getParent().getParent().getParent().getFileName().toString();

		Path blockstate = Paths.get(blockstateDir.toString(),blockName+".json");
		if(!Files.exists(blockstate)){
			Logger.getLogger(PaneMaker.class.getName()).log(Level.INFO, "Writing file {0}", blockstate);
			Files.newBufferedWriter(blockstate).append(TEMPLATE_BLOCKSTATE.replace(KEY_MODID, modid).replace(KEY_NAME, blockName)).close();
		}
		Path itemmodel = Paths.get(itemModelsDir.toString(), blockName + ".json");
		if (!Files.exists(itemmodel)) {
			Logger.getLogger(PaneMaker.class.getName()).log(Level.INFO, "Writing file {0}", itemmodel);
			Files.newBufferedWriter(itemmodel).append(
					TEMPLATE_ITEMMODEL.replace(KEY_NAME, blockName).replace(KEY_MODID, modid))
					.close();
		}
		Path blockmodel = Paths.get(modelsDir.toString(), blockName + "_n.json");
		if (!Files.exists(blockmodel)) {
			Logger.getLogger(PaneMaker.class.getName()).log(Level.INFO, "Writing file {0}", blockmodel);
			Files.newBufferedWriter(blockmodel).append(
					TEMPLATE_BLOCKMODEL.replace(KEY_DIR, "n").replace(KEY_NAME, blockName).replace(KEY_MODID, modid))
					.close();
		}
		blockmodel = Paths.get(modelsDir.toString(), blockName + "_ns.json");
		if (!Files.exists(blockmodel)) {
			Logger.getLogger(PaneMaker.class.getName()).log(Level.INFO, "Writing file {0}", blockmodel);
			Files.newBufferedWriter(blockmodel).append(
					TEMPLATE_BLOCKMODEL.replace(KEY_DIR, "ns").replace(KEY_NAME, blockName).replace(KEY_MODID, modid))
					.close();
		}
		blockmodel = Paths.get(modelsDir.toString(), blockName + "_ne.json");
		if (!Files.exists(blockmodel)) {
			Logger.getLogger(PaneMaker.class.getName()).log(Level.INFO, "Writing file {0}", blockmodel);
			Files.newBufferedWriter(blockmodel).append(
					TEMPLATE_BLOCKMODEL.replace(KEY_DIR, "ne").replace(KEY_NAME, blockName).replace(KEY_MODID, modid))
					.close();
		}
		blockmodel = Paths.get(modelsDir.toString(), blockName + "_nse.json");
		if (!Files.exists(blockmodel)) {
			Logger.getLogger(PaneMaker.class.getName()).log(Level.INFO, "Writing file {0}", blockmodel);
			Files.newBufferedWriter(blockmodel).append(
					TEMPLATE_BLOCKMODEL.replace(KEY_DIR, "nse").replace(KEY_NAME, blockName).replace(KEY_MODID, modid))
					.close();
		}
		blockmodel = Paths.get(modelsDir.toString(), blockName + "_nsew.json");
		if (!Files.exists(blockmodel)) {
			Logger.getLogger(PaneMaker.class.getName()).log(Level.INFO, "Writing file {0}", blockmodel);
			Files.newBufferedWriter(blockmodel).append(
					TEMPLATE_BLOCKMODEL.replace(KEY_DIR, "nsew").replace(KEY_NAME, blockName).replace(KEY_MODID, modid))
					.close();
		}
	}
	
}
