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
package cyano.mmu.util;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Christopher Collin Hall
 */
public class GUIHelper {
	
	public static File askForFolder(File rootDir){
		JFileChooser jfc = new JFileChooser(rootDir);
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int action = jfc.showOpenDialog(null);
		if(action == JFileChooser.CANCEL_OPTION) return null;
		return jfc.getSelectedFile();
	}
	
	
	public static File askForFile(File rootDir){
		JFileChooser jfc = new JFileChooser(rootDir);
		int action = jfc.showOpenDialog(null);
		if(action == JFileChooser.CANCEL_OPTION) return null;
		return jfc.getSelectedFile();
	}
	
	public static File[] askForFiles(File rootDir){
		JFileChooser jfc = new JFileChooser(rootDir);
		jfc.setMultiSelectionEnabled(true);
		int action = jfc.showOpenDialog(null);
		if(action == JFileChooser.CANCEL_OPTION) return null;
		return jfc.getSelectedFiles();
	}
}
