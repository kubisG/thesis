package cz.osu.core;

import javax.inject.Inject;

import cz.osu.core.exception.FileProviderException;
import org.springframework.stereotype.Component;

/**
 * Project: thesis
 * Created by Jakub on 13. 3. 2017.
 */

@Component
public class Composer {

    @Inject
    private TestSuitLoader testSuitLoader;

    @Inject
    private TestSuitsProvider testSuitsProvider;

    public void run() throws FileProviderException {
        //fileLoader.setFiles(fileProvider.getFilesFromDirectory("/"));
    }

}
