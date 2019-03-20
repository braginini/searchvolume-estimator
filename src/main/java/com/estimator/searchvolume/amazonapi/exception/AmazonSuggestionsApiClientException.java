package com.estimator.searchvolume.amazonapi.exception;

/**
 * Any kind of exceptions that might occur while calling Amazon Suggestion API (network, API, response parsing, etc)
 */
public class AmazonSuggestionsApiClientException extends RuntimeException {

    /**
     * Wraps original exception with clarification message
     *
     * @param message
     * @param cause
     */
    public AmazonSuggestionsApiClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception with clarification message
     *
     * @param message
     */
    public AmazonSuggestionsApiClientException(final String message) {
        super(message);
    }
}