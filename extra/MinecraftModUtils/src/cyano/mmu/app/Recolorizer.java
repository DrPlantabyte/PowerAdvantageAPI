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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author cybergnome
 */
public class Recolorizer {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		File[] images = GUIHelper.askForFiles(new File(System.getProperty("user.dir")), "png");
		Map<BufferedImage,File> imageSet = new HashMap<>();
		BufferedImage timg = null;
		for(File f : images){
			System.out.println("loading image: "+f);
			try {
				BufferedImage img = ImageIO.read(f);
				imageSet.put(img, f);
				if(timg == null){
					timg = img;
				}
			} catch (IOException ex) {
				Logger.getLogger(Recolorizer.class.getName()).log(Level.SEVERE, "Failed to read image file "+f, ex);
			}
		}
		final BufferedImage testImage = resizeImage(timg);
		JFrame window = new JFrame();
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane,BoxLayout.Y_AXIS));
		JPanel row1 = new JPanel();
		row1.setLayout(new BoxLayout(row1,BoxLayout.X_AXIS));
		JPanel row2 = new JPanel();
		row2.setLayout(new BoxLayout(row2,BoxLayout.X_AXIS));
		JPanel row3 = new JPanel();
		row3.setLayout(new BoxLayout(row3,BoxLayout.X_AXIS));
		JPanel row4 = new JPanel();
		row4.setLayout(new BoxLayout(row4,BoxLayout.X_AXIS));
		JPanel row5 = new JPanel();
		row5.setLayout(new BoxLayout(row5,BoxLayout.X_AXIS));
		
		final JLabel inputImg = new JLabel(new ImageIcon(testImage));
		inputImg.setSize(256,256);
		inputImg.setPreferredSize(new Dimension(256,256));
		row1.add(inputImg);
		row1.add(new JLabel("   =>   "));
		final JColorChooser cc = new JColorChooser();
		cc.setColor(127, 127, 127);
		row2.add(cc);
		final JLabel outputImg = new JLabel(new ImageIcon(setColor(testImage,cc.getColor(),0.0,1.0)));
		outputImg.setSize(256,256);
		outputImg.setPreferredSize(new Dimension(256,256));
		row1.add(outputImg);
		
		final AtomicReference<Double> contrast = new AtomicReference<>(1.0);
		final AtomicReference<Double> brightness = new AtomicReference<>(0.0);
		final JSlider contrastSlider = new JSlider(0,200,100);
		contrastSlider.addChangeListener((ChangeEvent e)->{
			contrast.set(contrastSlider.getValue() / 100.0);
			outputImg.setIcon(new ImageIcon(setColor(testImage,cc.getColor(),brightness.get(),contrast.get())));
		});
		row4.add(new JLabel("Less Contrast"));
		row4.add(contrastSlider);
		row4.add(new JLabel("More Contrast"));
		
		final JSlider brightnessSlider = new JSlider(0,200,100);
		brightnessSlider.addChangeListener((ChangeEvent e)->{
			brightness.set(brightnessSlider.getValue() / 100.0 - 1.0);
			outputImg.setIcon(new ImageIcon(setColor(testImage,cc.getColor(),brightness.get(),contrast.get())));
		});
		row3.add(new JLabel("Less Bright"));
		row3.add(brightnessSlider);
		row3.add(new JLabel("More Bright"));
		
		cc.getSelectionModel().addChangeListener((ChangeEvent e)->{
			outputImg.setIcon(new ImageIcon(setColor(testImage,cc.getColor(),brightness.get(),contrast.get())));
		});
		
		JButton ok = new JButton("Apply Color to Images");
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener((ActionEvent ae)->{
			System.exit(0);
		});
		ok.addActionListener((ActionEvent ae)->{
                    imageSet.keySet().stream().forEach((in) -> {
                        BufferedImage out = setColor(in,cc.getColor(),brightness.get(),contrast.get());
                        try {
                            ImageIO.write(out, "png", imageSet.get(in));
                        } catch (IOException ex) {
                            Logger.getLogger(Recolorizer.class.getName()).log(Level.SEVERE, "Failed to write to image file "+imageSet.get(in), ex);
                        }
                    });
                    System.exit(0);
		});
		row5.add(cancel);
		row5.add(ok);
		
		mainPane.add(row1);
		mainPane.add(row2);
		mainPane.add(row3);
		mainPane.add(row4);
		mainPane.add(row5);
		window.getContentPane().add(mainPane);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	public static BufferedImage setColor(BufferedImage bimg, Color c,Double brightness, Double contrast){
		BufferedImage img = new BufferedImage(bimg.getWidth(),bimg.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				img.setRGB(x, y, bimg.getRGB(x, y));
			}
		}
		
		float[] hsb = new float[3];
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
		for(int y = 0; y < img.getHeight(); y++){
			for(int x = 0; x < img.getWidth(); x++){
				img.setRGB(x, y, recolorPixel(img.getRGB(x, y),hsb[0], hsb[1], hsb[2],brightness.floatValue(),contrast.floatValue()));
			}
		}
		return img;
	}
	
	public static BufferedImage resizeImage(BufferedImage bimg){
		BufferedImage img = new BufferedImage((int)(256.0*bimg.getWidth()/bimg.getHeight()),256,BufferedImage.TYPE_INT_ARGB);
		
		for(int y = 0; y < img.getHeight(); y++){
			double yScale = y / (double)img.getHeight();
			for(int x = 0; x < img.getWidth(); x++){
				double xScale = x / (double)img.getWidth();
				img.setRGB(x, y, bimg.getRGB((int)(bimg.getWidth() * xScale), (int)(bimg.getHeight() * yScale)));
			}
		}
		return img;
	}
	
	public static int recolorPixel(int px, float hue, float saturation, float brightness, float brightnessAdj, float contrastAdj){
		int alphaChannel = px & 0xFF000000;
		float[] hsb = new float[3];
		Color.RGBtoHSB((px>>16)&0xFF, (px>>8)&0xFF, px&0xFF, hsb);
		// skip pixels that are already colored)
		if(hsb[1] > 0.1f){
			return px;
		}
		hsb[0] = hue;
		hsb[1] = saturation;
		hsb[2] = hsb[2] * (brightness + 0.5f) + brightnessAdj;
		hsb[2] = ( (hsb[2] - 0.5f) * contrastAdj ) + 0.5f;
		if(hsb[2] > 1)hsb[1] -= hsb[2] - 1;
		return ((Color.HSBtoRGB(hsb[0], clamp(hsb[1]), clamp(hsb[2]))) & 0x00FFFFFF) | alphaChannel;
	}
	
	private static float clamp(float f){
		if(f < 0)return 0;
		if(f > 1) return 1;
		return f;
	}
}

