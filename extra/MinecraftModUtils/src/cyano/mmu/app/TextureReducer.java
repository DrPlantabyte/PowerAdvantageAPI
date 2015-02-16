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

import cyano.mmu.util.GUIHelper;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author cybergnome
 */
public class TextureReducer {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		final File[] inputFiles = GUIHelper.askForFiles("Select 64x64 texture images",new File(System.getProperty("user.dir")));
		if(inputFiles == null || inputFiles.length == 0) return;
		final File outputDirX32 = GUIHelper.askForFolder("Select output folder for 32x32 textures",inputFiles[0].getParentFile());
		final File outputDirX16 = GUIHelper.askForFolder("Select output folder for 16x16 textures",inputFiles[0].getParentFile());
		
		for(File f : inputFiles){
			try{
				BufferedImage x64 = ImageIO.read(f);
				BufferedImage x32 = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
				BufferedImage x16 = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
				for(int x = 0; x < 32; x++){
					for(int y = 0; y < 32; y++){
						x32.setRGB(x, y, x64.getRGB(2*x, 2*y));
					}
				}
				for(int x = 0; x < 16; x++){
					for(int y = 0; y < 16; y++){
						x16.setRGB(x, y, x64.getRGB(4*x, 4*y));
					}
				}
				ImageIO.write(x32, "png", Paths.get(outputDirX32.getPath(),f.getName()).toFile());
				ImageIO.write(x16, "png", Paths.get(outputDirX16.getPath(),f.getName()).toFile());
				System.out.println("Shrank image file "+f);
			}catch(IOException ex){
				Logger.getLogger(TextureReducer.class.getName()).log(Level.SEVERE, "Failed to process image file "+f, ex);
			}
		}
		
	}

}
