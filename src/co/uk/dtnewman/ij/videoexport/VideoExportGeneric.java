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

import java.util.Arrays;

import io.humble.video.Codec;

public class VideoExportGeneric {
	

	public static String[] formatArray = new String[] { "avi", "mov", "mp4", "mkv", "wmv" };
	public static Codec.ID[] simpleApprovedCodecs = new Codec.ID[] {Codec.ID.CODEC_ID_H264, Codec.ID.CODEC_ID_RAWVIDEO, Codec.ID.CODEC_ID_JPEG2000, Codec.ID.CODEC_ID_PNG, Codec.ID.CODEC_ID_MSMPEG4V1, Codec.ID.CODEC_ID_MPEG4 };

	public static String getExtension(String filename) {
		String extension = null;
		if (filename.contains(".")) {
			extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		}
		return extension;
	}
	
	public static boolean isApprovedCodec(Codec.ID codecID) {
		if(Arrays.asList(simpleApprovedCodecs).contains(codecID)) {
			return true;
		}
		else {
		
		return false;
		}
	}

}
