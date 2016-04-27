/*
 * Copyright (C) 2015 cybergnome
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cyano.mmu.app;

import cyano.mmu.util.FormGUI;
import cyano.mmu.util.GUIHelper;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cybergnome
 */
public class SlabMaker {

	final static String TEMPLATE_BLOCKSTATE = "{\n" +
"    \"variants\": {\n" +
"        \"position=bottom\": { \"model\": \"mineralogy:andesite_halfslab\" },\n" +
"        \"position=top\": { \"model\": \"mineralogy:andesite_halfslab\", \"x\":180 },\n" +
"        \"position=double\": { \"model\": \"mineralogy:andesite_doubleslab\" }\n" +
"    }\n" +
"}";
	
	final static String TEMPLATE_HALF = "{\n" +
"    \"parent\": \"mineralogy:block/slab_half\",\n" +
"    \"textures\": {\n" +
"        \"bottom\": \"mineralogy:blocks/andesite\",\n" +
"        \"top\": \"mineralogy:blocks/andesite\",\n" +
"        \"side\": \"mineralogy:blocks/andesite\"\n" +
"    }\n" +
"}";
	final static String TEMPLATE_DOUBLE = "{\n" +
"    \"parent\": \"mineralogy:block/slab_double\",\n" +
"    \"textures\": {\n" +
"        \"bottom\": \"mineralogy:blocks/andesite\",\n" +
"        \"top\": \"mineralogy:blocks/andesite\",\n" +
"        \"side\": \"mineralogy:blocks/andesite\"\n" +
"    }\n" +
"}";
	final static String TEMPLATE_ITEM = "{\n" +
"    \"parent\": \"mineralogy:block/andesite_halfslab\",\n" +
"    \"display\": {\n" +
"        \"thirdperson\": {\n" +
"            \"rotation\": [ 10, -45, 170 ],\n" +
"            \"translation\": [ 0, 1.5, -2.75 ],\n" +
"            \"scale\": [ 0.375, 0.375, 0.375 ]\n" +
"        }\n" +
"    }\n" +
"}";
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		// Make stair files for provided textures
		final File[] files = GUIHelper.askForFiles(new File(System.getProperty("user.dir")));
		Map<String,String> data = FormGUI.getUserInput("mod ID");
		
		for(File f : files){
			try{
				createStairFiles(f,data.get("mod ID"));
			} catch(IOException ex){
				Logger.getLogger(SlabMaker.class.getName()).log(Level.SEVERE, "Failed to generate model files for texture "+f,ex);
			}
		}
		
		System.exit(0);
	}
	
	public static void createStairFiles(File textureFile, String modID) throws IOException{
		Path rootDir  = textureFile.toPath() // texture/blocks/texture.png
				.getParent() // texture/blocks
				.getParent() // texture
				.getParent(); // <root>
		String texName = textureFile.getName().substring(0,textureFile.getName().lastIndexOf("."));
		
		System.out.println(texName+"_slab");
		
		Path blockstateFile = Paths.get(rootDir.toString(), "blockstates",texName+"_slab.json");
		Path blockModelHalfslabFile = Paths.get(rootDir.toString(), "models","block",texName+"_halfslab.json");
		Path blockModelDoubleslabFile = Paths.get(rootDir.toString(), "models","block",texName+"_doubleslab.json");
		Path itemModelFile = Paths.get(rootDir.toString(), "models","item",texName+"_slab.json");
		
		writeStringToFile(TEMPLATE_BLOCKSTATE
				.replace("mineralogy", modID)
				.replace("andesite", texName),
				blockstateFile);
		
		
		writeStringToFile(TEMPLATE_HALF
				.replace("mineralogy", modID)
				.replace("andesite", texName),
				blockModelHalfslabFile);
		
		
		writeStringToFile(TEMPLATE_DOUBLE
				.replace("mineralogy", modID)
				.replace("andesite", texName),
				blockModelDoubleslabFile);
		
		
		writeStringToFile(TEMPLATE_ITEM
				.replace("mineralogy", modID)
				.replace("andesite", texName),
				itemModelFile);
		
	}
	
	protected static void writeStringToFile(String s, Path p) throws IOException{
		List<String> l = new ArrayList<>(1);
		l.add(s);
		Files.write(p, l, Charset.forName("UTF-8"));
	}
}
