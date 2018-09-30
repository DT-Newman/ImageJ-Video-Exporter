ImageJ-Video-Exporter
===================

Video Exporter for ImageJ utilizing the humble-video library

## PLUGINS > DN_TOOLS > Video Export

I wrote this plugin out of frustration with the fact that a lot of video export plugins for imagej, export videos using codecs which are often impractical (RAW - large files) or likely to cause compatibility issues. This is despite the fact that the underlying library used often implements more convenient choices such as H.264.

This aim of this plugin is to expose as many of the options provided by the underlying video library to the user as possible. Caution : While the user should be prevented from making option combinations which will result in errors in video exportation, they are however still quite likely.

There is a simple export option, which provides a smaller sub-set of options.

Development largely occurs as a result of me dogfooding this plugin, adding features that I need at the time and fixing it when it breaks on me. So please let me know of any bugs or suggestions!

# Building
ImageJ-Video-Exporter can be build via maven, simply run the following command in the root directory.

```
mvn install
```

# Installing

This plugin requires the https://github.com/artclarke/humble-video Library to be included in your plugins folder. 

Generic Library:

humble-video-noarch-x.x.x.jar 

A system specific native library :

humble-video-arch-[OS specific]-x.x.x.jar

Operating System | 64 bit | 32 bit (i686)
-----------------|--------|-------------|
Windows | x86_64-w64-migw32 | i686-w64-migw32
Mac OS X | x86_64-apple-darwin12 | i686-apple-darwin-12
Linux | x86_64-pc-linux-gnu6 | i686-pc-linux-gnu6



# Exporting a video

The plugin exports the active window at the time the plugin is first called.

## Simple

If you just want to export a video quickly I would recommend using the Simple export option, and the following settings as a starting point:

* *Format - MP4
* *Codec - libx264
* *Framerate - (Frames per second) - Number of frames per second

## Advanced

* *Format 
* *Codec - libx264
* *Pixel Format -  Probably best to leave as the option automatically selected
* *Framerate - (Frames per second) - Number of frames per second
* *Bitrate - (bits per second) -  0 Lets the encoder decide.

The following options don't matter if you are not resizing the image during export. It is probably a better idea to do as much editing as possible in ImageJ before you export your video.

* Anti-Aliasing - Allow smoothing upon resizing
* Interpolation - Set method of smoothing: Nearest Neighbour (Good, fast), Bilinear (Better, slower), Bicubic (Best, slowest)

# Known Issues

* While humble-video supports GIF output, this is currently not an option
