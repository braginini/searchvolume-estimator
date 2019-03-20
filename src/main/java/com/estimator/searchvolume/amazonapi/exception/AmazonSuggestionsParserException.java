package com.estimator.searchvolume.amazonapi.exception;

/**
 * Any kind of exceptions that might occur while parsing. Mostly JSON related.
 */
public class AmazonSuggestionsParserException extends AmazonSuggestionsApiClientException {

    /**
     * Wraps original exception with clarification message
     *
     * @param message
     * @param cause
     */
    public AmazonSuggestionsParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}