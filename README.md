ImageJ-Video-Exporter
===================

Video Exporter for ImageJ utilizing the humble-video library

## PLUGINS > DN_TOOLS > Video Export

I wrote this plugin out of frustration with the fact that a lot of  plugins for imagej export videos using codecs which are often impractical (RAW - large files) or likley to cause compatibility issues rather than the most natural choice - H.264 (libx264). 

This aim of this plugin is to expose as many of the configuration options provided by the underlying video library to the user as possible. **Caution** : The user should be prevented from making option combinations which will result in errors in video exporation, but they are however still quite likley.

There is a simple export option, which only provides a smaller sub-set of options.

Development largely occurs as a result of me dogfooding this plugin, adding features that I need at the time and fixing it when it breaks on me. So please let me know of any bugs or suggestions!


#Installing

This plugin requires the https://github.com/artclarke/humble-video Library to be included in your plugins folder. The required files are provided in the libs folder (you can just copy all files in this folder to you plugin directory).

Generic Library:

humble-video-all-x.x.x.jar 

A system specific native library :

humble-video-arch-[OS specific]-x.x.x.jar

Operating System | 64 bit | 32 bit (i686)
-----------------|--------|-------------|
Windows | x86_64-w64-migw32 | i686-w64-migw32
Mac OS X | x86_64-apple-darwin12 | i686-apple-darwin-12
Linux | x86_64-pc-linux-gnu6 | i686-pc-linux-gnu6



#Exporting a video - Advanced

The plugin exports the active window at the time the plugin is first called.

If you just want to export a video quickly I would recommend the following as a starting point.

* Format - MP4
* Codec - libx264
* Framerate - (Frames per second) - Number of frames per second
* Bitrate - (bits per second) - Leave Blank or as 0 

The following options don't matter if you don't resize the image during export.

* Anti-Aliasing - Allow smoothing upon resizing
* Interpolation - Set method of smoothing: Nearest Neighbour (Good, fast), Bilinear (Better, slower), Bicubic (Best, slowest)

#Known Issues

* While humble-video supports GIF output, this is currently not an option
