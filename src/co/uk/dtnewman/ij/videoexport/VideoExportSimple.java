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
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

@Plugin(type = Command.class, menuPath ="Plugins>DN Tools>Video Export>1.Simple")
public class VideoExportSimple implements Command {
	@Override
	public void run() {
		try {
			ImagePlus image = WindowManager.getCurrentImage();
			if (image == null) {
				IJ.error("There is currently no active image open");
			}
			else {
			VideoExportWindowSimple.create(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
			IJ.log("error: " + e.getMessage());
		}
	}

	

}
