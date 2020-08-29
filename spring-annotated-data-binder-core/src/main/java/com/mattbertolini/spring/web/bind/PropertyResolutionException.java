package com.mattbertolini.spring.web.bind;

/**
 * Exception thrown when a known exception is thrown during property resolution.
 */
public class PropertyResolutionException extends RuntimeException {
    public PropertyResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
