package co.uk.dtnewman.ij.videoexport;

public class VideoExportGeneric {

	public static String[] formatArray = new String[] { "avi", "gif", "mov", "mp4", "mkv", "wmv" };

	public static String getExtension(String filename) {
		String extension = null;
		if (filename.contains(".")) {
			extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		}
		return extension;
	}

}
