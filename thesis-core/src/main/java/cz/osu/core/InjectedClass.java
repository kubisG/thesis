package cz.osu.core;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Created by Jakub on 11. 2. 2017.
 */

@Component
public class InjectedClass {

    @Inject
    TestClass testClass;

    public Long dummy(){
        return testClass.getMockedId();
    }

}
