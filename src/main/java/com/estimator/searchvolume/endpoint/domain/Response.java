package com.estimator.searchvolume.endpoint.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {

    @JsonProperty("Keyword")
    private final String keyword;

    @JsonProperty("score")
    private final int score;

    public Response(final String keyword, final int score) {
        this.keyword = keyword;
        this.score = score;
    }
}
