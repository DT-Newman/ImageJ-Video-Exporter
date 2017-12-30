package co.uk.dtnewman.ij.videoexport;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

public class VideoExport implements PlugIn {

	public void run(String arg) {
		try {
			ImagePlus image = WindowManager.getCurrentImage();
			VideoExportWindow.create(image);
		} catch (Exception e) {
			e.printStackTrace();
			IJ.log("error: " + e.getMessage());
		}
	}

}
