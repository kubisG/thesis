package cz.osu;

import cz.osu.core.Composer;
import cz.osu.core.exception.FileLoaderException;
import cz.osu.core.exception.FileProviderException;
import cz.osu.core.recorder.Recorder;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

/**
 * Hello world!
 *
 */
public class App {

    // TODO: 27. 6. 2017 will be removed
    public static final String MOCKED_INPUT_FILES_DIR = "C:\\Users\\Jakub\\input-files";

    // TODO: 27. 6. 2017 will be removed
    public static final String MOCKED_OUTPUT_FILES_DIR = "C:\\Users\\Jakub\\output-files";

    public static void main( String[] args ) throws FileProviderException, IOException,
                                                    URISyntaxException, NoSuchMethodException,
                                                    InterruptedException, InstantiationException,
                                                    AWTException, FileLoaderException,
                                                    IllegalAccessException, InvocationTargetException {
        // load application context
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"application-context.xml"});
        // get bean main bean
        Composer composer = (Composer) context.getBean("Composer");
        // start process
        composer.process(MOCKED_INPUT_FILES_DIR, MOCKED_OUTPUT_FILES_DIR);
        // JAR address
        System.out.println("BLAAAAAA: " + App.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

}
