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

import java.awt.EventQueue;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import ij.IJ;
import ij.ImagePlus;
import io.humble.video.Codec;
import io.humble.video.Codec.ID;
import io.humble.video.MediaDescriptor;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;

public class VideoExportWindowSimple extends JFrame {

	private JPanel contentPane;
	private JLabel preLoadStatus;
	private JLabel lblCodec;
	private JComboBox<String> comboBox, selectionCodec;
	private static int stacksize, width, height;
	private boolean hwlock = false;
	private JButton btnExport;
	public String[] codecArray = new String[] { "Waiting for preload" };
	public String[] pixelFormatArray = new String[] {"Waiting for preload"};
	private String[] interpolationNameArray = new String[] { "Nearest Neighbour", "Bilnear", "Bicubic" };
	private Object[] interpolationArray = new Object[] { RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR, RenderingHints.VALUE_INTERPOLATION_BICUBIC };
	private ID[] idArray;
	

	/**
	 * Launch the application.
	 */

	public static void create(ImagePlus image) {
		stacksize = image.getImageStackSize();
		width = image.getWidth();
		height = image.getHeight();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VideoExportWindowSimple frame = new VideoExportWindowSimple(image);
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
	public VideoExportWindowSimple(ImagePlus image) {

		setTitle("Daniel's Video Exporter");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 325, 225);

		NumberFormat format = NumberFormat.getInstance();
		format.setGroupingUsed(false);
		NumberFormatter integerFormatter = new NumberFormatter(format);
		integerFormatter.setValueClass(Integer.class);
		integerFormatter.setMinimum(0);
		integerFormatter.setMaximum(Integer.MAX_VALUE);
		integerFormatter.setAllowsInvalid(false);
		// If you want the value to be committed on each keystroke instead of focus lost
		integerFormatter.setCommitsOnValidEdit(true);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		preLoadStatus = new JLabel("Preload Status");
		preLoadStatus.setBounds(0, 159, 302, 14);
		contentPane.add(preLoadStatus);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 70, 458, 4);
		contentPane.add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(0, 276, 458, 5);
		contentPane.add(separator_1);

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<>(co.uk.dtnewman.ij.videoexport.VideoExportGeneric.formatArray));
		comboBox.setBounds(90, 22, 200, 20);
		comboBox.setSelectedIndex(3);
		contentPane.add(comboBox);

		JLabel lblFormat = new JLabel("Format");
		lblFormat.setBounds(5, 25, 46, 14);
		contentPane.add(lblFormat);

		lblCodec = new JLabel("Codec");
		lblCodec.setBounds(5, 56, 46, 14);
		contentPane.add(lblCodec);

		JLabel lblFramerate = new JLabel("Framerate");
		lblFramerate.setBounds(5, 89, 67, 14);
		contentPane.add(lblFramerate);

		JFormattedTextField framerateText = new JFormattedTextField(integerFormatter);
		framerateText.setText("5");
		framerateText.setBounds(90, 86, 200, 20);
		contentPane.add(framerateText);

		btnExport = new JButton("Export");
		btnExport.setBounds(201, 124, 89, 23);
		contentPane.add(btnExport);

		selectionCodec = new JComboBox<String>();
		selectionCodec.setModel(new DefaultComboBoxModel<>(codecArray));
		selectionCodec.setBounds(90, 54, 200, 20);
		contentPane.add(selectionCodec);

		// Run preload operation on background thread
		Thread backgroundPreload = new Thread(this::backgroundPreload);
		backgroundPreload.start();
		

		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// fc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
				String formatExtension = "" + comboBox.getSelectedItem();
				fc.setFileFilter(new FileNameExtensionFilter(formatExtension + " file", formatExtension));
				int returnval = fc.showSaveDialog(VideoExportWindowSimple.this);
				String outputFilePath;
				if (fc.getSelectedFile().isFile() && VideoExportGeneric
						.getExtension(fc.getSelectedFile().getAbsolutePath()).equals(formatExtension)) {
					outputFilePath = fc.getSelectedFile().getAbsolutePath();
				} else {
					outputFilePath = fc.getSelectedFile().getAbsolutePath() + "." + formatExtension;
				}

				File outputfile = new File(outputFilePath);

				if (outputfile.isDirectory()) {
					IJ.error("The selected file is a directory");
					return;
				}

				if (outputfile.exists()) {
					int dialogResult = JOptionPane.showConfirmDialog(VideoExportWindowSimple.this,
							"This file already exists, overwrite?");
					if (dialogResult != JOptionPane.YES_OPTION) {
						return;
					}
				}

				// Export Video Now
				if (!isPosInteger(framerateText.getText())) {
					IJ.log("Height, Width, bitrate and framerate must be positive integers if set");
					return;
				}

				int framerate = Integer.parseInt(framerateText.getText());
				int widthint = width;
				int heightint = height;

				if (widthint < 1 || heightint < 1) {
					IJ.log("Width and height must be greater than 0");
					return;
				}

				VideoHandle videoHandle = new VideoHandle();
				videoHandle.setHeight(heightint);
				videoHandle.setWidth(widthint);
				

				if (framerate < 1) {
					framerate = 5;
				}
				videoHandle.setFrameRate(framerate);
				videoHandle.setOutFile(outputfile.getAbsolutePath());
				videoHandle.setCodecname(codecArray[selectionCodec.getSelectedIndex()]);
			

				VideoExporterGUI.exportVideo(videoHandle, image);

			}
		});

	}

	private boolean isPosInteger(String s) {
		return s != null && s.matches("\\d+");
	}

	private float convertToFloat(Object obj) {
		if (obj instanceof Integer)
			return (float) (Integer) obj;
		else if (obj instanceof Float)
			return (Float) obj;
		else
			throw new IllegalArgumentException("Unknown type " + obj.getClass());
	}

	public void backgroundPreload() {
		preLoadStatus.setText("Preload Running...");
		Collection<MuxerFormat> muxerformats = MuxerFormat.getFormats();
		preLoadStatus.setText("Preload Complete!");
		updateCodecSelection();
		btnExport.setEnabled(true);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCodecSelection();
			}
		});
		

	}
	
	

	public void updateCodecSelection() {

		final MuxerFormat format = MuxerFormat.guessFormat(null,
				"something." + comboBox.getItemAt(comboBox.getSelectedIndex()), null);
		Collection<ID> supportedCodecs = format.getSupportedCodecs();
		idArray = supportedCodecs.stream().map(Codec::findEncodingCodec).filter(c -> c != null)
				.filter(c -> VideoExportGeneric.isApprovedCodec(c.getID()))
				.sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).map(Codec::getID).toArray(ID[]::new);

		String defaultcodecName = Codec.findEncodingCodec(format.getDefaultVideoCodecId()).getName();
		int defaultCodecIndex = 0;
		int codecNumber = idArray.length;
		if (codecNumber == 0) {
			codecArray = new String[1];
			codecArray[0] = defaultcodecName;
			defaultCodecIndex = 0;

		} else {
			codecArray = new String[codecNumber];
			for (int i = 0; i < codecNumber; i++) {
				ID codecID = idArray[i];
				codecArray[i] = Codec.findEncodingCodec(codecID).getName();
				if (codecArray[i].equals(defaultcodecName)) {
					defaultCodecIndex = i;
				}

			}
		}
		selectionCodec.setModel(new DefaultComboBoxModel<>(codecArray));
		selectionCodec.setSelectedIndex(defaultCodecIndex);

	}
}
