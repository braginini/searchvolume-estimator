package com.estimator.searchvolume.amazonapi;

import com.estimator.searchvolume.amazonapi.exception.AmazonSuggestionsApiClientException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class AmazonSuggestionsApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSuggestionsApiClient.class);

    //https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=keyword
    private static final String urlTemplate = "https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=%s";

    private final OkHttpClient httpClient;

    private final AmazonSuggestionsParser responseParser;

    AmazonSuggestionsApiClient(final OkHttpClient httpClient, final AmazonSuggestionsParser responseParser) {
        this.httpClient = httpClient;
        this.responseParser = responseParser;
    }

    /**
     * Creates an instance with  default http client and default parser.
     */
    public AmazonSuggestionsApiClient() {
        this(new OkHttpClient.Builder().build(), new AmazonSuggestionsParser());
    }

    public List<String> lookupSuggestions(final String keyword) {

        final String url = String.format(urlTemplate, keyword);

        LOGGER.debug("calling amazon suggestions api for [keyword: {}] [URL: {}]", keyword, url);

        //todo URL encode keyword
        final Request request = new Request.Builder().url(url).build();

        //todo add retries
        try (final Response response = httpClient.newCall(request).execute()) {

            LOGGER.debug("got response from amazon suggestions api for [keyword: {}] [URL: {}]", keyword, url);

            return handleResponse(response, keyword);

        } catch (IOException e) {
            throw new AmazonSuggestionsApiClientException("error while calling Amazon Suggestions API for [keyword: " + keyword + "]", e);
        }

    }

    private List<String> handleResponse(final Response response, final String keyword) throws IOException {

        if (response.code() != 200) {
            throw new AmazonSuggestionsApiClientException("non 200 status code returned from Amazon Suggestions API for [keyword: " +
                    "]" + keyword + "[status: " + response.code() + "]");
        }

        if (response.body() == null) {
            throw new AmazonSuggestionsApiClientException("empty returned returned from Amazon Suggestions API for [keyword: " +
                    "]" + keyword + " [status: " + response.code() + "]");
        }

        return responseParser.parse(response.body().string());

    }


}
