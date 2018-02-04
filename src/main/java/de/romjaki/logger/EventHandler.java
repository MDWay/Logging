package de.romjaki.logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Created by RGR on 17.08.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface EventHandler {
}
