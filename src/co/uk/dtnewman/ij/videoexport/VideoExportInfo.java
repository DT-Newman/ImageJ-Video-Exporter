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
