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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 *
 * @author Christopher Collin Hall
 */
public class FormGUI {
	final Map<String,String> data;
	
	protected FormGUI(Map<String,String> startingValues){
		data = new LinkedHashMap<>();
		data.putAll(startingValues);
	}
	
	protected void showModalDialog(){
		JDialog d = new JDialog();
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane,BoxLayout.Y_AXIS));
		Map<String,JTextField> uiMap = new HashMap<>();
		data.keySet().stream().forEach((String key)->{
			JPanel row = new JPanel();
			row.setLayout(new FlowLayout());
			row.add(new JLabel(key+" ="));
			JTextField input = new JTextField(20);
			input.setText(data.get(key));
			uiMap.put(key, input);
			row.add(input);
			mainPane.add(row);
		});
		JButton doneButton = new JButton("Done");
		doneButton.addActionListener((ActionEvent ae)->d.setVisible(false));
		mainPane.add(doneButton);
		
		d.getContentPane().add(new JScrollPane(mainPane));
		
		d.pack();
		d.setLocationRelativeTo(null);
		d.setModal(true);
		d.setVisible(true);
		
		uiMap.keySet().stream().forEach((String key)->{
			data.put(key, uiMap.get(key).getText());
		});
	}
	
	public static Map<String,String> getUserInput(String... prompts){
		return getUserInput(Arrays.asList(prompts));
	}
	
	public static Map<String,String> getUserInput(List<String> prompts){
		Map<String,String> startingValues = new LinkedHashMap<>();
		prompts.stream().forEach((String s)->startingValues.put(s, ""));
		return getUserInput(startingValues);
	}
	
	public static Map<String,String> getUserInput(Map<String,String> startingValues){
		FormGUI form = new FormGUI(startingValues);
		form.showModalDialog();
		return Collections.unmodifiableMap(form.data);
	}
	
	@Deprecated public static void main(String[] args){
		System.out.println("Testing class: "+FormGUI.class.getName());
		final Map<String,String> map = getUserInput("a","b","c");
		map.keySet().forEach((String k)->{System.out.println(k+"="+map.get(k));});
		System.exit(0);
	}
	
}
