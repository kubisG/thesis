package cz.osu.core;

import io.humble.video.customio.HumbleIO;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
public class Recorder extends Thread {

    private List<BufferedImage> screenCaptures = new ArrayList<>();

    public List<BufferedImage> getScreenCaptures() {
        return screenCaptures;
    }

    // TODO: 15. 6. 2017 implementation here
    public void export() {}

    @Override
    public void run() {
        int i = 0;

        while (!Thread.interrupted()) {
            System.out.println("record: " + i);
            i++;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
        System.out.println("record stopped");
    }
}
