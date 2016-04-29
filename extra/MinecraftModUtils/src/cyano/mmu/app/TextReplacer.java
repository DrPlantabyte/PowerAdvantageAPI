/*
 * Copyright (C) 2015 Christopher Collin Hall
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Replaces text in all files
 * @author Christopher Collin Hall
 */
public class TextReplacer {

	final static Charset CHARSET = Charset.forName("UTF-8");
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		final File[] files = GUIHelper.askForFiles(new File(System.getProperty("user.dir")));
		Map<String,String> data = FormGUI.getUserInput("find text","replacement");
		final String inputText = data.get("find text");
		final String replacementText = data.get("replacement");
		Arrays.stream(files).forEach((File input)->{
			try{
				List<String> content = Files.readAllLines(input.toPath(),CHARSET);
				for(int i = 0; i < content.size(); i++){
					content.set(i, content.get(i).replace(inputText, replacementText));
				}
				Path inputPath = input.toPath();
				Path outputPath = Paths.get(input.getParent(),input.getName().replace(inputText, replacementText));
				Files.write(outputPath, content, CHARSET);
				if(inputPath.equals(outputPath) == false){
					Files.deleteIfExists(inputPath);
				}
			}catch(IOException ex){
				Logger.getLogger(TextReplacer.class.getName()).log(Level.SEVERE, "Error", ex);
			}
		});
		
		System.exit(0);
	}
	
}
