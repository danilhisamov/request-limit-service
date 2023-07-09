package ru.danilkhisamov.requestmonitoring.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for limiting requests on particular method.
 * <p>Methods annotated with @RequestLimited must have {@link jakarta.servlet.http.HttpServletRequest HttpServletRequest} in their arguments.
 *
 * <p>Example:
 * {@code
 *     @RequestLimited
 *     public ResponseEntity<Void> getOperation(HttpServletRequest request) {...}
 * }
 *
 * @author Danil Khisamov
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimited {
}
