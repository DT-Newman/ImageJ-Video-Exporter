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

public class VideoExportWindowAdvanced extends JFrame {

	private JPanel contentPane;
	private JLabel preLoadStatus;
	private JLabel lblHeight;
	private JLabel lblCodec;
	private JComboBox<String> comboBox, selectionCodec, comboPixelFormat;
	private JCheckBox chckbxForceOddDimensions;
	private static int stacksize, width, height;
	private boolean hwlock = false;
	private JButton btnExport;
	public String[] codecArray = new String[] { "Waiting for preload" };
	public String[] pixelFormatArray = new String[] {"Waiting for preload"};
	private String[] interpolationNameArray = new String[] { "Nearest Neighbour", "Bilnear", "Bicubic" };
	private Object[] interpolationArray = new Object[] { RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR, RenderingHints.VALUE_INTERPOLATION_BICUBIC };
	private int defaultCodecIndex, defaultPixelFormatIndex = 0;
	private ID[] idArray;
	private io.humble.video.PixelFormat.Type[] pixelTypeArray;
	

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
					VideoExportWindowAdvanced frame = new VideoExportWindowAdvanced(image);
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
	public VideoExportWindowAdvanced(ImagePlus image) {

		setTitle("Daniel's Video Exporter");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 474, 356);

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
		preLoadStatus.setBounds(5, 314, 302, 14);
		contentPane.add(preLoadStatus);

		JLabel lblNewLabel = new JLabel("Width");
		lblNewLabel.setBounds(5, 45, 46, 14);
		contentPane.add(lblNewLabel);

		lblHeight = new JLabel("Height");
		lblHeight.setBounds(5, 14, 46, 14);
		contentPane.add(lblHeight);

		JCheckBox chckbxAspectRatio = new JCheckBox("Lock Aspect Ratio");
		chckbxAspectRatio.setSelected(true);
		chckbxAspectRatio.setBounds(226, 9, 180, 23);
		contentPane.add(chckbxAspectRatio);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 70, 458, 4);
		contentPane.add(separator);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(0, 276, 458, 5);
		contentPane.add(separator_1);

		JFormattedTextField inputHeight = new JFormattedTextField(integerFormatter);
		inputHeight.setBounds(70, 11, 99, 20);
		inputHeight.setValue(height);
		contentPane.add(inputHeight);

		JFormattedTextField inputWidth = new JFormattedTextField(integerFormatter);
		inputWidth.setBounds(70, 42, 99, 20);
		inputWidth.setValue(width);
		contentPane.add(inputWidth);

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<>(co.uk.dtnewman.ij.videoexport.VideoExportGeneric.formatArray));
		comboBox.setBounds(90, 82, 200, 20);
		comboBox.setSelectedIndex(3);
		contentPane.add(comboBox);

		JLabel lblFormat = new JLabel("Format");
		lblFormat.setBounds(5, 85, 46, 14);
		contentPane.add(lblFormat);

		lblCodec = new JLabel("Codec");
		lblCodec.setBounds(5, 115, 46, 14);
		contentPane.add(lblCodec);

		JLabel lblFramerate = new JLabel("Framerate");
		lblFramerate.setBounds(5, 178, 67, 14);
		contentPane.add(lblFramerate);

		JLabel lblBitrate = new JLabel("Bitrate");
		lblBitrate.setBounds(5, 206, 46, 14);
		contentPane.add(lblBitrate);

		JFormattedTextField framerateText = new JFormattedTextField(integerFormatter);
		framerateText.setText("5");
		framerateText.setBounds(90, 175, 200, 20);
		contentPane.add(framerateText);

		JFormattedTextField bitrateText = new JFormattedTextField(integerFormatter);
		bitrateText.setBounds(90, 203, 200, 20);
		bitrateText.setText("0");
		contentPane.add(bitrateText);

		btnExport = new JButton("Export");
		btnExport.setBounds(369, 283, 89, 23);
		contentPane.add(btnExport);

		selectionCodec = new JComboBox<String>();
		selectionCodec.setModel(new DefaultComboBoxModel<>(codecArray));
		selectionCodec.setBounds(90, 113, 200, 20);
		contentPane.add(selectionCodec);
		
		comboPixelFormat = new JComboBox<String>();
		comboPixelFormat.setModel(new DefaultComboBoxModel<>(pixelFormatArray));
		comboPixelFormat.setBounds(90, 141, 200, 20);
		contentPane.add(comboPixelFormat);

		JLabel lblInterpolation = new JLabel("Interpolation");
		lblInterpolation.setBounds(6, 235, 75, 14);
		contentPane.add(lblInterpolation);

		JComboBox<String> comboInterpolation = new JComboBox<String>();
		comboInterpolation.setModel(new DefaultComboBoxModel<>(interpolationNameArray));
		comboInterpolation.setBounds(91, 232, 200, 20);
		contentPane.add(comboInterpolation);

		JCheckBox chckbxAntialiasing = new JCheckBox("Anti-Aliasing");
		chckbxAntialiasing.setBounds(91, 259, 151, 23);
		contentPane.add(chckbxAntialiasing);
		
		
		JLabel lblPixelFormat = new JLabel("Pixel Format");
		lblPixelFormat.setBounds(5, 148, 89, 14);
		contentPane.add(lblPixelFormat);
		
		chckbxForceOddDimensions = new JCheckBox("Force Odd Dimensions");
		chckbxForceOddDimensions.setBounds(226, 40, 180, 23);
		contentPane.add(chckbxForceOddDimensions);

		// Run preload operation on background thread
		Thread backgroundPreload = new Thread(this::backgroundPreload);
		backgroundPreload.start();

		PropertyChangeListener listenInputHeight = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (chckbxAspectRatio.isSelected()) {
					if (!hwlock) {
						hwlock = true;
						float specifiedHeight = convertToFloat(inputHeight.getValue());
						int newWidth = (int) (width * (specifiedHeight / height));
						inputWidth.setValue(newWidth);
						hwlock = false;
					}

				}

			}
		};
		inputHeight.addPropertyChangeListener(listenInputHeight);

		PropertyChangeListener listenInputWidth = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (chckbxAspectRatio.isSelected()) {
					if (!hwlock) {
						hwlock = true;
						float specifiedWidth = convertToFloat(inputWidth.getValue());
						int newHeight = (int) (height * (specifiedWidth / width));
						inputHeight.setValue(newHeight);
						hwlock = false;
					}
				}
			}
		};
		inputWidth.addPropertyChangeListener(listenInputWidth);

		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				// fc.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
				String formatExtension = "" + comboBox.getSelectedItem();
				fc.setFileFilter(new FileNameExtensionFilter(formatExtension + " file", formatExtension));
				int returnval = fc.showSaveDialog(VideoExportWindowAdvanced.this);
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
					int dialogResult = JOptionPane.showConfirmDialog(VideoExportWindowAdvanced.this,
							"This file already exists, overwrite?");
					if (dialogResult != JOptionPane.YES_OPTION) {
						return;
					}
				}

				// Export Video Now
				if (!isPosInteger(inputWidth.getText()) || !isPosInteger(inputHeight.getText())
						|| !isPosInteger(framerateText.getText())) {
					IJ.log("Height, Width, bitrate and framerate must be positive integers if set");
					return;
				}

				int framerate = Integer.parseInt(framerateText.getText());
				int widthint = Integer.parseInt(inputWidth.getText());
				int heightint = Integer.parseInt(inputHeight.getText());

				if (widthint < 1 || heightint < 1) {
					IJ.log("Width and height must be greater than 0");
					return;
				}

				VideoHandle videoHandle = new VideoHandle();
				videoHandle.setHeight(heightint);
				videoHandle.setWidth(widthint);
				String bitratestr = bitrateText.getText();

				if (!bitratestr.equals("")) {
					int bitrateint = Integer.parseInt(bitrateText.getText());
					if (bitrateint != 0) {
						videoHandle.setBitRate(bitrateint);
					}
				}

				if (framerate < 1) {
					framerate = 5;
				}
				videoHandle.setFrameRate(framerate);
				videoHandle.setOutFile(outputfile.getAbsolutePath());
				videoHandle.setCodecname(codecArray[selectionCodec.getSelectedIndex()]);
				videoHandle.setAntialias(chckbxAntialiasing.isSelected());
				videoHandle.setInterpolation(interpolationArray[comboInterpolation.getSelectedIndex()]);
				
				PixelFormat.Type pixelFormat = pixelTypeArray[comboPixelFormat.getSelectedIndex()];
				videoHandle.setPixelFormat(pixelFormat);
				
				videoHandle.setForcedOdd(chckbxForceOddDimensions.isSelected());

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
		updatePixelFormats();
		btnExport.setEnabled(true);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCodecSelection();
			}
		});
		
		selectionCodec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updatePixelFormats();
			}
		});
		

	}
	
	public void updatePixelFormats() {
		
		
	 Codec codec = Codec.findEncodingCodecByName(codecArray[selectionCodec.getSelectedIndex()]);
	 int noofpixel = codec.getNumSupportedVideoPixelFormats();
	 if(noofpixel > 0) {
	 ArrayList<PixelFormat.Type> alist=new ArrayList<PixelFormat.Type>();
		for(int i = 0; i < noofpixel; i++) {
			
			try {
				alist.add(codec.getSupportedVideoPixelFormat(i));
				}
				catch(IllegalArgumentException ex) {
					continue;
				}
		}
		pixelTypeArray = alist.toArray(new PixelFormat.Type[alist.size()]);
	 }
		else {
			pixelTypeArray = PixelFormat.Type.values();
		}
		int size = pixelTypeArray.length;
		pixelFormatArray = new String[size];
		for(int i = 0; i < size; i++) {
			pixelFormatArray[i] = pixelTypeArray[i].toString();
			if(pixelFormatArray[i] == io.humble.video.PixelFormat.Type.PIX_FMT_YUV420P.name()) {
				defaultPixelFormatIndex = i; 
			}
		}
	 
		
		comboPixelFormat.setModel(new DefaultComboBoxModel<>(pixelFormatArray));
		comboPixelFormat.setSelectedIndex(defaultPixelFormatIndex);
		
	
	}

	public void updateCodecSelection() {

		final MuxerFormat format = MuxerFormat.guessFormat(null,
				"something." + comboBox.getItemAt(comboBox.getSelectedIndex()), null);
		Collection<ID> supportedCodecs = format.getSupportedCodecs();
		idArray = supportedCodecs.stream().map(Codec::findEncodingCodec).filter(c -> c != null)
				.filter(c -> c.getType() == MediaDescriptor.Type.MEDIA_VIDEO)
				.sorted((c1, c2) -> c1.getName().compareTo(c2.getName())).map(Codec::getID).toArray(ID[]::new);

		String defaultcodecName = Codec.findEncodingCodec(format.getDefaultVideoCodecId()).getName();

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
