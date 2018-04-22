package co.uk.dtnewman.ij.videoexport;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

@Plugin(type = Command.class, menuPath ="Plugins>DN Tools>Video Export>Advanced")
public class VideoExport implements Command {
	@Override
	public void run() {
		try {
			ImagePlus image = WindowManager.getCurrentImage();
			if (image == null) {
				IJ.error("There is currently no active image open");
			}
			else {
			VideoExportWindow.create(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
			IJ.log("error: " + e.getMessage());
		}
	}

	

}
