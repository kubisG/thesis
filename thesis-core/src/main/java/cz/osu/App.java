package cz.osu;

import cz.osu.core.UserDocumentationMaker;
import cz.osu.core.exception.FileLoaderException;
import cz.osu.core.exception.FileProviderException;
import cz.osu.core.loader.ConfigLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) throws FileProviderException, IOException,
                                                    URISyntaxException, NoSuchMethodException,
                                                    InterruptedException, InstantiationException,
                                                    AWTException, FileLoaderException,
                                                    IllegalAccessException, InvocationTargetException {

        LOGGER.debug("----------- Start of application ------------- " + args[0]);
        System.out.println("----------------------- : " + args[0]);
        // load application context
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"application-context.xml"});
        // get user documentation maker bean
        UserDocumentationMaker userDocumentationMaker = (UserDocumentationMaker) context.getBean("userDocumentationMaker");
        // get config loader bean
        ConfigLoader configLoader = (ConfigLoader) context.getBean("configLoader");

        // load paths to directories
        LOGGER.debug("----------- Trying to load config.xml -------------");
        String pathToConfig = args[0];
        Map<String, String> paths = configLoader.loadPathsFromXML(pathToConfig);
        LOGGER.debug("----------- File config.xml has been successfully loaded -------------");

        // set paths to directories
        String inputDirectory = paths.get("input");
        String outputDirectory = paths.get("output");

        // start process
        LOGGER.debug("----------- Start of process -----------");
        userDocumentationMaker.process(inputDirectory, outputDirectory);
        LOGGER.debug("----------- End of process --------------");

        LOGGER.debug("----------- End of application --------------");
        // JAR address
        System.out.println("BLAAAAAA: " + App.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

}
