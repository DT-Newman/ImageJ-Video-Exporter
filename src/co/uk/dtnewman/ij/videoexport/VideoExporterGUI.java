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
			image = image.flatten();
			int stacksize = image.getStackSize();
			IJ.log("");
			currentTask.setText("Copying Imagestack");
			ImageStack imagestack = image.getImageStack();

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

	}

}
