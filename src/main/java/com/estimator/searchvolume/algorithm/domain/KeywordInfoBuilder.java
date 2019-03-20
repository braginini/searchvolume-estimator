package com.estimator.searchvolume.algorithm.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Builds {@link KeywordInfo} out of keyword, suggestions and other meta data like suggestions of shorter keywords.
 * Also finds prefixes for the original keyword
 *
 * 1. splits keyword into words
 * 2. calculates tail size
 * 3. lower cases keywords and suggestions
 *
 * Possible improvements:
 * 1. typo correction??
 * 2. splitting logic (now only space, but could be more spaces)
 *
 *
 */
public class KeywordInfoBuilder {

    public KeywordInfo build(final String keyword, final List<String> suggestions, final Optional<KeywordInfo> parentKeywordInfo) {

        final String[] words = keyword.split("\\s");

        final int tailSize = words.length;

        return new KeywordInfo(keyword, Arrays.asList(words), tailSize, suggestions, parentKeywordInfo);
    }
}
