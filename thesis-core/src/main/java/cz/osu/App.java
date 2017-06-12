package cz.osu;

import cz.osu.core.Composer;
import cz.osu.core.exception.FileProviderException;
import cz.osu.core.model.Variable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) throws FileProviderException, IOException, URISyntaxException {
        System.err.println("start");
        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[] {"application-context.xml"});

        System.out.println("BLAAAAAA: " + App.class.getProtectionDomain().getCodeSource().getLocation().getPath());


        URL scannedUrl = ClassLoader.getSystemResource(".");
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<Class<?>>();


        classes.stream().forEach(System.out::println);

        System.err.println("1");
        System.err.println("2: " + scannedUrl.getPath());
        System.err.println(scannedDir);
        // System.out.println(jarDir.getAbsolutePath());

        Composer composer = (Composer) context.getBean("Composer");
        composer.run();

        System.out.println( "Hello World!" + composer.toString());
    }

}
