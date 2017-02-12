package cz.osu;

import cz.osu.core.InjectedClass;
import cz.osu.core.TestClass;
import javafx.event.ActionEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[] {"application-context.xml"});

        InjectedClass injectedClass= (InjectedClass) context.getBean("injectedClass");
        Long tmp = injectedClass.dummy();

        System.out.println( "Hello World!" + tmp);
    }

}
