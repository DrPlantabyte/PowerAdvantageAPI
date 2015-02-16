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
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

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
	public static File askForFolder(String title, File rootDir){
		JFileChooser jfc = new JFileChooser(rootDir);
		jfc.setDialogTitle(title);
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
	public static File askForFile(String title,File rootDir){
		JFileChooser jfc = new JFileChooser(rootDir);
		jfc.setDialogTitle(title);
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
	public static File[] askForFiles(String title, File rootDir){
		JFileChooser jfc = new JFileChooser(rootDir);
		jfc.setDialogTitle(title);
		jfc.setMultiSelectionEnabled(true);
		int action = jfc.showOpenDialog(null);
		if(action == JFileChooser.CANCEL_OPTION) return null;
		return jfc.getSelectedFiles();
	}
	
	public static File askForFile(File rootDir, String... allowedSuffixes){
		final String[] suffixes = allowedSuffixes;
		final String description = Arrays.toString(suffixes);
		JFileChooser jfc = new JFileChooser(rootDir);
		jfc.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				if(f.isDirectory())return true;
				if(f.getName().contains(".") == false) return false;
				String suffix = f.getName().substring(f.getName().lastIndexOf("."));
				for(String s : suffixes){
					if(suffix.equalsIgnoreCase(s)) return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return description;
			}
		});
		int action = jfc.showOpenDialog(null);
		if(action == JFileChooser.CANCEL_OPTION) return null;
		return jfc.getSelectedFile();
	}
	
	public static File[] askForFiles(String title, File rootDir, String... allowedSuffixes){
		final String[] suffixes = allowedSuffixes;
		final String description = Arrays.toString(suffixes);
		JFileChooser jfc = new JFileChooser(rootDir);
		jfc.setDialogTitle(title);
		jfc.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				if(f.isDirectory())return true;
				if(f.getName().contains(".") == false) return false;
				String suffix = f.getName().substring(f.getName().lastIndexOf(".")+1);
				for(String s : suffixes){
					if(suffix.equalsIgnoreCase(s)) return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return description;
			}
		});
		jfc.setMultiSelectionEnabled(true);
		int action = jfc.showOpenDialog(null);
		if(action == JFileChooser.CANCEL_OPTION) return null;
		return jfc.getSelectedFiles();
	}
	
	public static File[] askForFiles(File rootDir, String... allowedSuffixes){
		final String[] suffixes = allowedSuffixes;
		final String description = Arrays.toString(suffixes);
		JFileChooser jfc = new JFileChooser(rootDir);
		jfc.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f) {
				if(f.isDirectory())return true;
				if(f.getName().contains(".") == false) return false;
				String suffix = f.getName().substring(f.getName().lastIndexOf(".")+1);
				for(String s : suffixes){
					if(suffix.equalsIgnoreCase(s)) return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return description;
			}
		});
		jfc.setMultiSelectionEnabled(true);
		int action = jfc.showOpenDialog(null);
		if(action == JFileChooser.CANCEL_OPTION) return null;
		return jfc.getSelectedFiles();
	}
	
	
}
