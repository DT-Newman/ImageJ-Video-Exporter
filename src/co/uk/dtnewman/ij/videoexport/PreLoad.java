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
