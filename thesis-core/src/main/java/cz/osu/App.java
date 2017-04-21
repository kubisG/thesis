package cz.osu;

import cz.osu.core.Composer;
import cz.osu.core.model.Variable;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App {

    public static void main( String[] args ) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[] {"application-context.xml"});

        Composer composer = (Composer) context.getBean("Composer");
        Integer i = 10;
        Variable v = new Variable("a", new Integer(20), Integer.class);
        Class clazz = Long.class;
        System.out.println(v.getValue().getClass());
        System.out.println(v.getType());
        System.out.println(clazz);



        System.out.println( "Hello World!" + composer.toString());
    }

}
