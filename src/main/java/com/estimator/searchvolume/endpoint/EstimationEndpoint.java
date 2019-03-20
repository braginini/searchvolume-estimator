package com.estimator.searchvolume.endpoint;

import com.estimator.searchvolume.algorithm.Algorithm;
import com.estimator.searchvolume.algorithm.domain.KeywordInfo;
import com.estimator.searchvolume.algorithm.domain.KeywordInfoBuilder;
import com.estimator.searchvolume.amazonapi.AmazonSuggestionsApiClient;
import com.estimator.searchvolume.amazonapi.exception.AmazonSuggestionsApiClientException;
import com.estimator.searchvolume.amazonapi.exception.AmazonSuggestionsParserException;
import com.estimator.searchvolume.endpoint.domain.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.RouteGroup;

import java.util.*;

import static spark.Spark.exception;
import static spark.Spark.get;

/**
 * Handles incoming requests for estimating search volume for a keyword.
 * <p>
 * <p>
 *
 * //TODO Improvements:
 * 1. validate request
 * 2. add exception mapping layer with meaningful messages
 * 3. call AmazonSuggestionsApiClient asynchronously
 * 4. clean up requested keyword (spaces, typos??), improve word splitting logic
 */
public class EstimationEndpoint implements RouteGroup {

    private static final Logger LOGGER = LoggerFactory.getLogger(EstimationEndpoint.class);

    private final Algorithm algorithm;

    private final AmazonSuggestionsApiClient amazonSuggestionsApiClient;

    private final ObjectMapper mapper;

    public EstimationEndpoint(final Algorithm algorithm, final AmazonSuggestionsApiClient amazonSuggestionsApiClient) {
        this(algorithm, amazonSuggestionsApiClient, new ObjectMapper());
    }

    EstimationEndpoint(final Algorithm algorithm,
                       final AmazonSuggestionsApiClient amazonSuggestionsApiClient,
                       final ObjectMapper mapper) {
        this.algorithm = algorithm;
        this.amazonSuggestionsApiClient = amazonSuggestionsApiClient;
        this.mapper = mapper;
    }

    @Override
    public void addRoutes() {

        exception(AmazonSuggestionsParserException.class, (exception, request, response) -> {
            //todo handle the exception here, do not show that we failed on Amazon response parsing, send just generic message
            LOGGER.error("error while handling request", exception);
            response.body("");
            response.status(500);
        });

        exception(AmazonSuggestionsApiClientException.class, (exception, request, response) -> {
            //todo handle the exception here, do not show that we failed on Amazon API call, send just generic message
            LOGGER.error("error while handling request", exception);
            response.body("");
            response.status(503);
        });

        exception(Exception.class, (exception, request, response) -> {
            //todo handle generic the exception here
            LOGGER.error("error while handling request", exception);
            response.body("");
            response.status(500);
        });

        get("", (request, response) -> {

            validateRequest(request);

            final String keyword = request.queryMap("keyword").value();

            final KeywordInfo keywordInfo = getSuggestionsTree(keyword);

            final double score = algorithm.evaluate(keywordInfo);

            response.header("Content-Type", "application/json; charset=utf-8");

            return mapper.writeValueAsString(new Response(keyword, (int) (score * 100)));

        });
    }

    private void validateRequest(final Request request) {
        //todo
    }

    private KeywordInfo getSuggestionsTree(final String originalKeyword) {

        //having original keyword, traverse prefix tree backwards up to keyword with single word

        final KeywordInfoBuilder keywordInfoBuilder = new KeywordInfoBuilder();

        KeywordInfo parentKeyword = null;
        //here is a very strict assumption that words are split by just 1 space, and keyword itself is clean
        //todo improve
        int index = originalKeyword.indexOf(" ");
        while (index > 0) {
            final String keyword = originalKeyword.substring(0, index);
            final List<String> suggestions = amazonSuggestionsApiClient.lookupSuggestions(keyword);
            parentKeyword = keywordInfoBuilder.build(keyword, suggestions, Optional.ofNullable(parentKeyword));
            index = originalKeyword.indexOf(" ", index + 1);
        }

        final List<String> suggestions = amazonSuggestionsApiClient.lookupSuggestions(originalKeyword);
        final KeywordInfo keywordInfo = keywordInfoBuilder.build(originalKeyword, suggestions, Optional.ofNullable(parentKeyword));

        return keywordInfo;
    }
}
