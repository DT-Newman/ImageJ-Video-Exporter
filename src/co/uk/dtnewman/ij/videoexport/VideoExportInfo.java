package co.uk.dtnewman.ij.videoexport;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import ij.IJ;
import io.humble.video.Codec;

@Plugin(type = Command.class, menuPath ="Plugins>DN Tools>Video Export>Info")
public class VideoExportInfo implements Command {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Codec codec = Codec.findEncodingCodec(Codec.ID.CODEC_ID_GIF);
		int noofpixel = codec.getNumSupportedVideoPixelFormats();
		
		for(int i = 0; i < noofpixel; i++) {
			IJ.log("Pixel fomat "+i+" : "+codec.getSupportedVideoPixelFormat(i));
		}
	}

}
