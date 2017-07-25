package cz.osu.core.recorder;

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
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

import cz.osu.core.util.PathBuilderUtils;

/**
 * Project: thesis
 * Created by Jakub on 1. 7. 2017.
 */
@Component
public final class Recorder implements Runnable {

    private static final int SNAPS_PER_SECOND = 20;

    private static final String VIDEO_FORMAT = "AVI";

    private static final String MOUSE_CURSOR = "cursor_5.png";

    private final Robot robot;

    private String outputFile;

    private CountDownLatch latch;

    private volatile boolean running;

    @Inject
    public Recorder(Robot robot) {
        this.robot = robot;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public void stopRecording() {
        running = false;
    }

    //Convert a BufferedImage of any type, to BufferedImage of a specified type.
    private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;

        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    // add mouse cursor into current image
    private void addMouseCursor(BufferedImage screenCapture) throws IOException {
        String cursorPath = PathBuilderUtils.buildPath("cursor", MOUSE_CURSOR);
        Image cursor = ImageIO.read(new File(cursorPath));
        int x = MouseInfo.getPointerInfo().getLocation().x;
        int y = MouseInfo.getPointerInfo().getLocation().y;

        Graphics2D graphics2D = screenCapture.createGraphics();
        graphics2D.drawImage(cursor, x, y, 24, 24, null);
    }


    private BufferedImage makeScreenCapture(Rectangle screenBounds) throws IOException {
        // make screen capture
        final BufferedImage screen = robot.createScreenCapture(screenBounds);
        // add mouse cursor to current screen
        addMouseCursor(screen);
        // convert and return screen
        return convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);

    }

    private Encoder createEncoder(MuxerFormat format) {
        // create codec according to format
        final Codec codec = Codec.findEncodingCodecByIntID(Codec.ID.CODEC_ID_H264.swigValue());
        // create encoder using above codec
        return Encoder.make(codec);
    }

    private void setEncoder(Encoder encoder, Rectangle screenBounds, Rational frameRate, PixelFormat.Type pixelFormat, MuxerFormat format) {
        // set encoder
        encoder.setWidth(screenBounds.width);
        encoder.setHeight(screenBounds.height);
        encoder.setPixelFormat(pixelFormat);
        encoder.setTimeBase(frameRate);
        //Some formats need global headers, and in that case you have to tell the encoder.
        if (format.getFlag(MuxerFormat.Flag.GLOBAL_HEADER))
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
    }

    private void encode(Encoder encoder, MediaPicture picture, MediaPacket packet, Muxer muxer) {
        do {
            encoder.encode(packet, picture);
            if (packet.isComplete())
                muxer.write(packet, false);
        } while (packet.isComplete());
    }

    private MediaPictureConverter createConverter(Rectangle screenBounds, MediaPicture picture) {
        final BufferedImage screen = robot.createScreenCapture(screenBounds);
        return MediaPictureConverterFactory.createConverter(convertToType(screen, BufferedImage.TYPE_3BYTE_BGR), picture);
    }

    private void openMuxer(Encoder encoder, Muxer muxer) {
        try {
            // open encoder
            encoder.open(null, null);
            // add encoder to muxer to enable process
            muxer.addNewStream(encoder);
            // open muxer
            muxer.open(null, null);
        } catch (InterruptedException | IOException e) {
            // clean up
            muxer.close();
            throw new IllegalStateException("Error while opening muxer");
        }
    }

    private void closeMuxer(Encoder encoder, Muxer muxer, MediaPacket packet) {
        // Encoders sometimes cache pictures. So they need to be flushed.
        // The convention is to pass in a null until the output is not complete.
        encode(encoder, null, packet, muxer);
        // close down muxer
        muxer.close();
    }

    private void record(int screenNumber, Rectangle screenBounds, Muxer muxer,
                       Encoder encoder, MediaPictureConverter converter,
                       MediaPicture picture, MediaPacket packet, Rational frameRate) throws IOException, InterruptedException {
        // Make the screen capture, add mouse cursor and convert image to TYPE_3BYTE_BGR
        final BufferedImage screen = makeScreenCapture(screenBounds);
        // processing picture which comes in as a parameter (void)
        converter.toPicture(picture, screen, screenNumber);
        // encode current picture
        encode(encoder, picture, packet, muxer);

        // now we'll sleep until it's time to take the next snapshot.
        System.out.println("Cycle ----------------------------- RECORD -------------------------: " + screenNumber);
        Thread.sleep((long) (1000 * frameRate.getDouble()));
    }

    @Override
    public void run() {
        // create and initialize all needed components to do the recording
        int screenNumber = 0;
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Rectangle screenBounds = new Rectangle(toolkit.getScreenSize());
        final Rational frameRate = Rational.make(1, SNAPS_PER_SECOND);
        final Muxer muxer = Muxer.make(outputFile, null, VIDEO_FORMAT);
        // formats
        final MuxerFormat format = muxer.getFormat();
        final PixelFormat.Type pixelFormat = PixelFormat.Type.PIX_FMT_YUV420P;
        final MediaPacket packet = MediaPacket.make();
        // create and set encoder
        final Encoder encoder = createEncoder(format);
        setEncoder(encoder, screenBounds, frameRate, pixelFormat, format);
        final MediaPicture picture = MediaPicture.make(encoder.getWidth(), encoder.getHeight(), pixelFormat);
        // create converter
        MediaPictureConverter converter = createConverter(screenBounds, picture);

        // open muxer to be able start process
        openMuxer(encoder, muxer);

        // release parent thread to start browser
        latch.countDown();

        // set flag
        running = true;

        // all components have been set, let's do the recording
        while (running) {
            try {
                record(screenNumber, screenBounds, muxer, encoder, converter, picture, packet, frameRate);
            } catch (IOException | InterruptedException e) {
                // clean up
                closeMuxer(encoder, muxer, packet);
                running = false;
            }
            // increment screen number
            screenNumber++;
        }
        // clean up
        closeMuxer(encoder, muxer, packet);
    }
}
