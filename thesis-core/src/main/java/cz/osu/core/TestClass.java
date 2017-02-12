package cz.osu.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * Created by Jakub on 11. 2. 2017.
 */

@Getter
@Setter
@Component
public class TestClass {

    private final static Long mockedId = 2L;

    private Long id;

    public Long getMockedId() {
        return mockedId;
    }
}
