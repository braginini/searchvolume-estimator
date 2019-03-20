package com.estimator.searchvolume.algorithm.factor;

import com.estimator.searchvolume.algorithm.domain.KeywordInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SuggestionsNumberFactor - more direct suggestions the better.
 *
 * //TODO Improvements:
 * 1. add unit test
 */
public class SuggestionsNumberFactor extends ScoringFactor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestionsNumberFactor.class);

    private final int maxSuggestions;

    public SuggestionsNumberFactor(final int maxSuggestions, final double weight) {
        super(weight);
        this.maxSuggestions = maxSuggestions;
    }

    public double calculateScore(final KeywordInfo keywordInfo) {

        final int suggestionsNum = Math.min(keywordInfo.getKeywordSuggestions().size(), maxSuggestions);

        final double score = suggestionsNum / (double) maxSuggestions;

        LOGGER.debug("[keyword: {}] [score: {}]", keywordInfo.getKeyword(), score);

        return score;
    }

}
