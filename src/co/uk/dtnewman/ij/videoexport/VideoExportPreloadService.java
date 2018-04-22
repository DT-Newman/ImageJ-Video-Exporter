package co.uk.dtnewman.ij.videoexport;

import java.util.Collection;

import org.scijava.event.EventHandler;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;
import org.scijava.ui.event.UIShownEvent;

import io.humble.video.MuxerFormat;

@Plugin(type = Service.class)
public class VideoExportPreloadService extends AbstractService {
	
	//TODO: Figure out how to force Humble Video to load in the background,
	// so that the use doesn't have to wait for the library to load later.
	
	@Parameter
	private LogService log;

	@EventHandler
	protected void onEvent(final UIShownEvent evt) {
		System.setProperty("scijava.log.level", "debug");
		Collection<MuxerFormat> muxerformats = MuxerFormat.getFormats();
		log.info("Preload Complete");
	}
	

}
