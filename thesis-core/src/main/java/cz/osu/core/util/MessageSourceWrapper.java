package cz.osu.core.util;

import java.util.Locale;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

/**
 * Project: thesis
 * Created by Jakub on 25. 3. 2017.
 */
@Component
public abstract class MessageSourceWrapper implements MessageSourceAware {

    /**
     * SYSTEM_LOCALE represents current system locale.
     */
    private static final Locale SYSTEM_LOCALE = Locale.getDefault();

    /**
     * messageSource is interface which allows me choose message
     * from properties file based on current system locales.
     */
    @Inject
    private MessageSource messageSource;

    /**
     * Method using message source instance to build message with given arguments.
     * @param property represents message template which is stored in message properties file.
     * @param params replace "placeholders" within message template.
     * @return String message which is composed from above params.
     */
    public String getMessage(String property, Object... params) {
        return messageSource.getMessage(property, params, SYSTEM_LOCALE);
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
