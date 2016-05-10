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
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christopher Collin Hall
 */
public class PipeRootModelMaker {
	final static Charset CHARSET = Charset.forName("UTF-8");
	
	final String[] orientations = {"0","densuw","desuw","dnu","ensuw","n","ns","nsuw","nsw","nuw","nw"};

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		
		final java.util.Map<String,String> params = FormGUI.getUserInput("name","radius");
		final File dir = GUIHelper.askForFolder(new File(System.getProperty("user.dir")));
		if(dir == null)System.exit(1);
		try{
		
			int r = Integer.parseInt(params.get("radius"));
			
			
		
		} catch(Exception ex){
			Logger.getLogger(PipeRootModelMaker.class.getName()).log(Level.SEVERE, "Error", ex);
			System.exit(ex.getClass().hashCode());
		}
		
		System.exit(0);
	}
	
	static class Box {
		int x1,x2,y1,y2,z1,z2;
	}
	
}
