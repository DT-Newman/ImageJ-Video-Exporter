package co.uk.dtnewman.ij.videoexport;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ij.IJ;
import io.humble.video.Codec;
import io.humble.video.Encoder;
import io.humble.video.MediaPacket;
import io.humble.video.MediaPicture;
import io.humble.video.Muxer;
import io.humble.video.MuxerFormat;
import io.humble.video.PixelFormat;
import io.humble.video.Rational;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

public class VideoHandle {
	private int width, height;
	private Object interpolation;
	private String outFile;
	private long firstTimeStamp = -1;
	private MediaPacket packet;
	private MediaPictureConverter converter = null;
	private MediaPicture picture;
	private int currentFrame;
	private Encoder encoder;
	private Muxer muxer;
	private int bitrate = 0;
	private String formatname = null;
	private String codecname = null;
	private Boolean antialias;
	private Rational frameRate;

	public void setHeight(int height) {
		this.height = height;
	}

	public void setInterpolation(Object interpolation) {
		this.interpolation = interpolation;
	}

	public void setAntialias(Boolean antialias) {
		this.antialias = antialias;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public String getOutFile() {
		return this.outFile;
	}

	public void setFormatname(String formatname) {
		this.formatname = formatname;
	}

	public void setCodecname(String codecname) {
		this.codecname = codecname;
	}

	public void setFrameRate(int rate) {
		frameRate = Rational.make(1, rate);
	}

	public void setBitRate(int bitrate) {
		this.bitrate = bitrate;
	}

	public void createVideoStream() throws InterruptedException, IOException {

		muxer = Muxer.make(outFile, null, formatname);

		final MuxerFormat format = muxer.getFormat();
		final Codec codec;
		if (codecname != null) {
			codec = Codec.findEncodingCodecByName(codecname);
		} else {
			codec = Codec.findEncodingCodec(format.getDefaultVideoCodecId());
		}

		encoder = Encoder.make(codec);
		encoder.setWidth(width);
		encoder.setHeight(height);

		// set bitrate if set
		if (bitrate != 0) {
			encoder.setProperty("b", bitrate);
		}
		
		//GIF codel doesn't support YUV420P as the pixel format
		//TODO: Check each codec supports YUV420P and if not change to something else. 
		final PixelFormat.Type pixelformat;
		if(codec.getID() == Codec.ID.CODEC_ID_GIF) {
			pixelformat = codec.getSupportedVideoPixelFormat(1);
			//pixelformat = PixelFormat.Type.PIX_FMT_RGB24;
			
		
		}
		else {
			//pixelformat = PixelFormat.Type.PIX_FMT_YUV420P;
			pixelformat = PixelFormat.Type.PIX_FMT_RGB24;
		}
		
		IJ.log("Codec is:" + codec);
		IJ.log("GIF Codec is:" + Codec.findEncodingCodec(Codec.ID.CODEC_ID_GIF));
		IJ.log("Pixel format is:" + pixelformat);
		
		
		
	
		encoder.setPixelFormat(pixelformat);
		encoder.setTimeBase(frameRate);

		if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
			encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);

		encoder.open(null, null);
		muxer.addNewStream(encoder);
		muxer.open(null, null);

		packet = MediaPacket.make();

		picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelformat);
		picture.setTimeBase(frameRate);

	}

	public BufferedImage convertToType(BufferedImage sourceImage, int targetType) {

		if (sourceImage.getType() == targetType && sourceImage.getHeight() == height
				&& sourceImage.getWidth() == width) {
			return sourceImage;
		}

		BufferedImage image = new BufferedImage(width, height, targetType);
		Graphics2D g = image.createGraphics();

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_INTERPOLATION, interpolation);
		g.setRenderingHints(rh);

		if (antialias) {
			rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} else {
			rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}

		g.setRenderingHints(rh);
		g.drawImage(sourceImage, 0, 0, width, height, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
		g.dispose();

		return image;
	}

	public void encode(BufferedImage bufferedImage) {

		final BufferedImage convertedImage = convertToType(bufferedImage, BufferedImage.TYPE_3BYTE_BGR);

		if (converter == null)
			converter = MediaPictureConverterFactory.createConverter(convertedImage, picture);
		converter.toPicture(picture, convertedImage, currentFrame++);

		do {
			encoder.encode(packet, picture);
			if (packet.isComplete())
				muxer.write(packet, false);
		} while (packet.isComplete());

	}

	public void closeVideoStream() {
		do {
			encoder.encode(packet, null);
			if (packet.isComplete())
				muxer.write(packet, false);
		} while (packet.isComplete());

		muxer.close();
	}

}
