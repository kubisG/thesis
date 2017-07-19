package cz.osu.core.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Project: thesis
 * Created by Jakub on 19. 7. 2017.
 */
public class PathBuilderUtils {

    public static String buildPath(String directory, String fileName) throws URISyntaxException {
        // path to jar location
        URI uri = PathBuilderUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        Path path = Paths.get(uri);
        // go to User documentation folder directory
        String rootDirectoryPath = path.getParent().toAbsolutePath().toString();
        // use default config.xml location
        return rootDirectoryPath.concat(File.separator)
                .concat(directory)
                .concat(File.separator)
                .concat(fileName);
    }
}
