package cz.osu.core;

import org.springframework.stereotype.Component;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
@Component
public class Recorder {

    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    // TODO: 15. 6. 2017 implementation here
    public void start() {}

    // TODO: 15. 6. 2017 implementation here
    public void stop() {}

    // TODO: 15. 6. 2017 implementation here
    public void export() {}
}
