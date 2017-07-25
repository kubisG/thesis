package cz.osu.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Project: thesis
 * Created by Jakub on 19. 7. 2017.
 */
public class PathBuilderUtils {

    public static String buildPath(String directory, String fileName) throws IOException {
        // path to jar location
        URI uri;
        try {
            uri = PathBuilderUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            throw new IOException("Cannot read path of application directory. Bad URI type.");
        }
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
