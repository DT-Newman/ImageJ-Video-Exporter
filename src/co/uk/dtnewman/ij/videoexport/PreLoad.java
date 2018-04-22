package co.uk.dtnewman.ij.videoexport;

import org.scijava.Context;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.service.Service;

import ij.IJ;
import io.humble.video.Codec;
import net.imagej.ImageJService;
@Plugin(type = Service.class)
public class PreLoad implements ImageJService {

	@Override
	public void initialize() {
		Thread preload = new Thread( new exportThread());
		preload.start();
	}
	
	private class exportThread implements Runnable {
		public void run() {
			Codec codec = Codec.findEncodingCodec(Codec.ID.CODEC_ID_GIF);
		}
	}
	
	@Override
	public Context context() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPriority(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PluginInfo<?> getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInfo(PluginInfo<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}
