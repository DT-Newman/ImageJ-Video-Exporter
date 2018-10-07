/*
 * Copyright (c) 2018, Daniel Newman.  All rights reserved.
 *   
 * This file is part of IJ-Video-Exporter.
 *
 * IJ-Video-Exporter is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IJ-Video-Exporter is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with IJ-Video-Exporter.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.uk.dtnewman.ij.videoexport;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

public class VideoExporterGUI extends JFrame {

	private JPanel contentPane;
	private JLabel currentTask;
	private JProgressBar progressBar;

	/**
	 * Launch the application.
	 */
	public static void exportVideo(VideoHandle videoHandle, ImagePlus image) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VideoExporterGUI frame = new VideoExporterGUI(videoHandle, image);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public VideoExporterGUI(VideoHandle videoHandle, ImagePlus image) {

		setTitle("Exporting Video...");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 449, 150);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		contentPane.add(progressBar, BorderLayout.SOUTH);

		currentTask = new JLabel("Current Task...");
		contentPane.add(currentTask, BorderLayout.CENTER);

		JLabel videoExport = new JLabel("Exporting Video to: " + videoHandle.getOutFile());
		contentPane.add(videoExport, BorderLayout.NORTH);

		Thread output = new Thread(new exportThread(videoHandle, image));
		output.start();
	}

	private class exportThread implements Runnable {
		private VideoHandle videoHandle;
		private ImagePlus image;


		public exportThread(VideoHandle videoHandle, ImagePlus image) {
			this.videoHandle = videoHandle;
			this.image = image;
		}

		public void run() {
			float currentProgressAmount = 0;


			//I imagine the below block of code is inefficient in terms of memory usage... should check!
			//TODO: Make each frame of the video on a frame by frame basis.


			ImagePlus workingImage = image.duplicate();
			ImageConverter ic = new ImageConverter(workingImage);
			currentTask.setText("Converting to RGB image");
			ic.convertToRGB();
			currentTask.setText("Copying Imagestack");
			int stacksize = workingImage.getStackSize();
			ImageStack imagestack = workingImage.getImageStack();
			
			if(!videoHandle.getForcedOdd()) {
				imagestack = makeEvenDimensions(videoHandle, imagestack);
			}

			

			try {
				videoHandle.createVideoStream();

				for (int i = 0; i < stacksize; i++) {
					currentTask.setText("Encoding Image: " + i + " / " + stacksize);
					ImageProcessor ip = imagestack.getProcessor(1 + i);
					videoHandle.encode(ip.getBufferedImage());
					currentProgressAmount = ((float) i / stacksize) * 100f;
					Integer currentProgressInt = (int) currentProgressAmount;
					progressBar.setValue(currentProgressInt);
				}
				currentTask.setText("Closing Video Stream");
				videoHandle.closeVideoStream();
				progressBar.setValue(100);
				currentTask.setText("Encoding Complete");

			} catch (Exception e) {
				e.printStackTrace();
				IJ.log("error: " + e.getMessage());
			}

		}
		
		private ImageStack makeEvenDimensions(VideoHandle videoHandle, ImageStack imagestack ) {
			//Make the export window even... 
			//if were not resizing the image and any dimension is odd then we want to resize the image
			int stackHeight = imagestack.getHeight();
			int stackWidth = imagestack.getWidth();
			boolean crop = false;
			if(videoHandle.getHeight() == stackHeight && videoHandle.getWidth() == stackWidth) {
				if(!VideoExportGeneric.isEven(stackHeight)) {
					crop = true;
					stackHeight--;
					videoHandle.setHeight(stackHeight);
				}
				if(!VideoExportGeneric.isEven(stackWidth)) {
					crop = true;
					stackWidth--;
					videoHandle.setWidth(stackWidth);
				}

				if(crop) {
					imagestack = imagestack.crop(0, 0, 0, stackWidth, stackHeight, imagestack.getSize() );
					IJ.log("Cropped image to make export dimensions even");
				}
			}
			else {
				if(!VideoExportGeneric.isEven(videoHandle.getHeight())) {
					crop = true;
					videoHandle.setHeight(videoHandle.getHeight() - 1);
					IJ.log("Reduced export height to make export dimensions even");
				}
				if(!VideoExportGeneric.isEven(videoHandle.getWidth())) {
					crop = true;
					videoHandle.setWidth(videoHandle.getWidth() - 1);
					IJ.log("Reduced export width to make export dimensions even");
				}

			}
			if(crop) {
				IJ.log("To maintain comapatibility with some formats, the export dimensions have been altered.");
				IJ.log("You can force odd dimension export in Advanced mode.");
			}
			return imagestack;
		}
		

	}

}
