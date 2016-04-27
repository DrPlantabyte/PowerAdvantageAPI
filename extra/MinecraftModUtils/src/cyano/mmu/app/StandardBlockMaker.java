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
public class StandardBlockMaker {

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
				supportBlockTexture(f.toPath());
			}
		}catch(IOException ex){
			Logger.getLogger(StandardBlockMaker.class.getName()).log(Level.SEVERE,"Error",ex);
			System.exit(ex.getClass().hashCode());
		}
	}

	
	private static void supportBlockTexture(Path blockTextureFile) throws IOException{
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
			Logger.getLogger(StandardBlockMaker.class.getName()).log(Level.INFO, "Writing file {0}", blockstate);
			Files.newBufferedWriter(blockstate).append("{\n"
					+ "    \"variants\": {\n"
					+ "        \"normal\": { \"model\": \"").append(modid).append(":").append(blockName).append("\" }\n"
					+ "    }\n"
					+ "}").close();
		}
		Path blockmodel = Paths.get(modelsDir.toString(), blockName + ".json");
		if (!Files.exists(blockmodel)) {
			Logger.getLogger(StandardBlockMaker.class.getName()).log(Level.INFO, "Writing file {0}", blockmodel);
			Files.newBufferedWriter(blockmodel).append("{\n"
					+ "    \"parent\": \"block/cube_all\",\n"
					+ "    \"textures\": {\n"
					+ "        \"all\": \"").append(modid).append(":blocks/").append(blockName).append("\"\n"
					+ "    }\n"
					+ "}").close();
		}
		Path itemmodel = Paths.get(itemModelsDir.toString(), blockName + ".json");
		if (!Files.exists(itemmodel)) {
			Logger.getLogger(StandardBlockMaker.class.getName()).log(Level.INFO, "Writing file {0}", itemmodel);
			Files.newBufferedWriter(itemmodel).append("{\n" +
"    \"parent\": \"").append(modid).append(":block/").append(blockName).append("\",\n" +
"    \"display\": {\n" +
"        \"thirdperson\": {\n" +
"            \"rotation\": [ 10, -45, 170 ],\n" +
"            \"translation\": [ 0, 1.5, -2.75 ],\n" +
"            \"scale\": [ 0.375, 0.375, 0.375 ]\n" +
"        }\n" +
"    }\n" +
"}").close();
		}
	}
	
}
