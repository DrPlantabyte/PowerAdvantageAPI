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
public class StairMaker {

	final static String TEMPLATE_BLOCKSTATE = "{\n" +
"    \"variants\": {\n" +
"        \"facing=east,half=bottom,shape=straight\":  { \"model\": \"brick_stairs\" },\n" +
"        \"facing=west,half=bottom,shape=straight\":  { \"model\": \"brick_stairs\", \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=south,half=bottom,shape=straight\": { \"model\": \"brick_stairs\", \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=north,half=bottom,shape=straight\": { \"model\": \"brick_stairs\", \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=east,half=bottom,shape=outer_right\":  { \"model\": \"brick_outer_stairs\" },\n" +
"        \"facing=west,half=bottom,shape=outer_right\":  { \"model\": \"brick_outer_stairs\", \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=south,half=bottom,shape=outer_right\": { \"model\": \"brick_outer_stairs\", \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=north,half=bottom,shape=outer_right\": { \"model\": \"brick_outer_stairs\", \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=east,half=bottom,shape=outer_left\":  { \"model\": \"brick_outer_stairs\", \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=west,half=bottom,shape=outer_left\":  { \"model\": \"brick_outer_stairs\", \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=south,half=bottom,shape=outer_left\": { \"model\": \"brick_outer_stairs\" },\n" +
"        \"facing=north,half=bottom,shape=outer_left\": { \"model\": \"brick_outer_stairs\", \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=east,half=bottom,shape=inner_right\":  { \"model\": \"brick_inner_stairs\" },\n" +
"        \"facing=west,half=bottom,shape=inner_right\":  { \"model\": \"brick_inner_stairs\", \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=south,half=bottom,shape=inner_right\": { \"model\": \"brick_inner_stairs\", \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=north,half=bottom,shape=inner_right\": { \"model\": \"brick_inner_stairs\", \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=east,half=bottom,shape=inner_left\":  { \"model\": \"brick_inner_stairs\", \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=west,half=bottom,shape=inner_left\":  { \"model\": \"brick_inner_stairs\", \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=south,half=bottom,shape=inner_left\": { \"model\": \"brick_inner_stairs\" },\n" +
"        \"facing=north,half=bottom,shape=inner_left\": { \"model\": \"brick_inner_stairs\", \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=east,half=top,shape=straight\":  { \"model\": \"brick_stairs\", \"x\": 180, \"uvlock\": true },\n" +
"        \"facing=west,half=top,shape=straight\":  { \"model\": \"brick_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=south,half=top,shape=straight\": { \"model\": \"brick_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=north,half=top,shape=straight\": { \"model\": \"brick_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=east,half=top,shape=outer_right\":  { \"model\": \"brick_outer_stairs\", \"x\": 180, \"uvlock\": true },\n" +
"        \"facing=west,half=top,shape=outer_right\":  { \"model\": \"brick_outer_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=south,half=top,shape=outer_right\": { \"model\": \"brick_outer_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=north,half=top,shape=outer_right\": { \"model\": \"brick_outer_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=east,half=top,shape=outer_left\":  { \"model\": \"brick_outer_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=west,half=top,shape=outer_left\":  { \"model\": \"brick_outer_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=south,half=top,shape=outer_left\": { \"model\": \"brick_outer_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=north,half=top,shape=outer_left\": { \"model\": \"brick_outer_stairs\", \"x\": 180, \"uvlock\": true },\n" +
"        \"facing=east,half=top,shape=inner_right\":  { \"model\": \"brick_inner_stairs\", \"x\": 180, \"uvlock\": true },\n" +
"        \"facing=west,half=top,shape=inner_right\":  { \"model\": \"brick_inner_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=south,half=top,shape=inner_right\": { \"model\": \"brick_inner_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=north,half=top,shape=inner_right\": { \"model\": \"brick_inner_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=east,half=top,shape=inner_left\":  { \"model\": \"brick_inner_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
"        \"facing=west,half=top,shape=inner_left\":  { \"model\": \"brick_inner_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
"        \"facing=south,half=top,shape=inner_left\": { \"model\": \"brick_inner_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
"        \"facing=north,half=top,shape=inner_left\": { \"model\": \"brick_inner_stairs\", \"x\": 180, \"uvlock\": true }\n" +
"    }\n" +
"}";
	
	final static String TEMPLATE_BRICK_STAIRS = "{\n" +
"    \"parent\": \"block/stairs\",\n" +
"    \"textures\": {\n" +
"        \"bottom\": \"blocks/brick\",\n" +
"        \"top\": \"blocks/brick\",\n" +
"        \"side\": \"blocks/brick\"\n" +
"    }\n" +
"}";
	final static String TEMPLATE_BRICK_INNER_STAIRS = "{\n" +
"    \"parent\": \"block/inner_stairs\",\n" +
"    \"textures\": {\n" +
"        \"bottom\": \"blocks/brick\",\n" +
"        \"top\": \"blocks/brick\",\n" +
"        \"side\": \"blocks/brick\"\n" +
"    }\n" +
"}";
	final static String TEMPLATE_BRICK_OUTER_STAIRS = "{\n" +
"    \"parent\": \"block/outer_stairs\",\n" +
"    \"textures\": {\n" +
"        \"bottom\": \"blocks/brick\",\n" +
"        \"top\": \"blocks/brick\",\n" +
"        \"side\": \"blocks/brick\"\n" +
"    }\n" +
"}";
	final static String TEMPLATE_STAIR_ITEM = "{\n" +
"    \"parent\": \"block/brick_stairs\",\n" +
"    \"display\": {\n" +
"        \"thirdperson\": {\n" +
"            \"rotation\": [ 10, -45, 170 ],\n" +
"            \"translation\": [ 0, 1.5, -2.75 ],\n" +
"            \"scale\": [ 0.375, 0.375, 0.375 ]\n" +
"        },\n" +
"        \"gui\": {\n" +
"            \"rotation\": [ 0, 180, 0 ]\n" +
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
				Logger.getLogger(StairMaker.class.getName()).log(Level.SEVERE, "Failed to generate model files for texture "+f,ex);
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
		
		System.out.println(texName+"_stairs");
		
		Path blockstateFile = Paths.get(rootDir.toString(), "blockstates",texName+"_stairs.json");
		Path blockModelNormalFile = Paths.get(rootDir.toString(), "models","block",texName+"_stairs.json");
		Path blockModelInnerFile = Paths.get(rootDir.toString(), "models","block",texName+"_inner_stairs.json");
		Path blockModelOuterFile = Paths.get(rootDir.toString(), "models","block",texName+"_outer_stairs.json");
		Path itemModelFile = Paths.get(rootDir.toString(), "models","item",texName+"_stairs.json");
		
		String replacementKey_bs = "brick_";
		String replacementValue_bs = modID+":"+texName+"_";
		writeStringToFile(TEMPLATE_BLOCKSTATE.replace(replacementKey_bs, replacementValue_bs),blockstateFile);
		
		String replacementKey_model = "blocks/brick";
		String replacementValue_model = modID+":blocks/"+texName;
		writeStringToFile(TEMPLATE_BRICK_STAIRS.replace(replacementKey_model, replacementValue_model),
				blockModelNormalFile);
		writeStringToFile(TEMPLATE_BRICK_INNER_STAIRS.replace(replacementKey_model, replacementValue_model),
				blockModelInnerFile);
		writeStringToFile(TEMPLATE_BRICK_OUTER_STAIRS.replace(replacementKey_model, replacementValue_model),
				blockModelOuterFile);
		
		String replacementKey_item = "block/brick_stairs";
		String replacementValue_item = modID+":block/"+texName+"_stairs";
		writeStringToFile(TEMPLATE_STAIR_ITEM.replace(replacementKey_item, replacementValue_item),
				itemModelFile);
	}
	
	protected static void writeStringToFile(String s, Path p) throws IOException{
		List<String> l = new ArrayList<>(1);
		l.add(s);
		Files.write(p, l, Charset.forName("UTF-8"));
	}
}
