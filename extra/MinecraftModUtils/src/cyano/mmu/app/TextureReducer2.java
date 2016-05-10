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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author cybergnome
 */
public class TextureReducer2 {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		File origin = new File(System.getProperty("user.home"));
		final Path inputDir = GUIHelper.askForFolder("Select input folder",origin).toPath();
		final Path outputDir = GUIHelper.askForFolder("Select output folder for halved textures",inputDir.getParent().toFile()).toPath();
		
		try {
			Files.walk(inputDir).forEach((Path inputFile)->{
				if(inputFile.toString().endsWith(".png")){
					// reduce texture
					Path outputFile = outputDir.resolve(inputDir.relativize(inputFile));
					System.out.println(String.format("Reading from '%s' and writing to '%s'",inputFile,outputFile));
					try {
						reduceImage(inputFile,outputFile);
					} catch (IOException ex) {
						Logger.getLogger(TextureReducer2.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			});
		} catch (IOException ex) {
			Logger.getLogger(TextureReducer2.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		
	}
	
	static void reduceImage(Path in, Path out) throws IOException{
		BufferedImage src = ImageIO.read(in.toFile());
		BufferedImage dest = new BufferedImage(src.getWidth()/2,src.getHeight()/2,BufferedImage.TYPE_INT_ARGB);
		for(int x = 0; x < src.getWidth()/2; x++){
			for(int y = 0; y < src.getHeight()/2; y++){
				dest.setRGB(x, y, src.getRGB(2*x, 2*y));
			}
		}
		Files.createDirectories(out.getParent());
		ImageIO.write(dest, "png", out.toFile());
	}

}
