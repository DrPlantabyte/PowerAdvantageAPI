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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Christopher Collin Hall
 */
public class PipeRootModelMaker {
	final static Charset charset = Charset.forName("UTF-8");

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		final java.util.Map<String,String> params = FormGUI.getUserInput("name","radius");
		final File dir = GUIHelper.askForFolder(new File(System.getProperty("user.dir")));
		if(dir == null)System.exit(1);
		try{
		
			int r = Integer.parseInt(params.get("radius"));
			Path srcDir = Paths.get("resources","models","block");
			List<Path> srcFiles = Files.list(srcDir).filter((Path f)->{return f.getFileName().toString().startsWith("pipe_");}).collect(Collectors.toList());
			
			srcFiles.stream().forEach((Path input)->{
				String filename = input.getFileName().toString().replace("pipe", params.get("name"));
				Path output = Paths.get(dir.toString(), filename);
				try{
					StringBuilder inBuffer = new StringBuilder();
					Files.readAllLines(input, charset).stream().forEach((String ln)->inBuffer.append(ln).append(System.lineSeparator()));
					String outBuffer = inBuffer.toString()
							.replace("6",String.valueOf(8-r))
							.replace("10",String.valueOf(8+r));
					Files.write(output, Arrays.asList(outBuffer.split(System.lineSeparator())));
				}catch(IOException ex){
					Logger.getLogger(PipeRootModelMaker.class.getName()).log(Level.SEVERE, "Error", ex);
				}
			});
		
		} catch(IOException ex){
			Logger.getLogger(PipeRootModelMaker.class.getName()).log(Level.SEVERE, "Error", ex);
			System.exit(ex.getClass().hashCode());
		}
		
		System.exit(0);
	}
	
}
