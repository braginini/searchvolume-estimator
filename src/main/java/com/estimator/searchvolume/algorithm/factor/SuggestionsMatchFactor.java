package com.estimator.searchvolume.algorithm.factor;

import com.estimator.searchvolume.algorithm.domain.KeywordInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * SuggestionsMatchFactor - original keyword appears in a lists of suggestions for itself,
 * Scoring logic also considers position in the list of suggestions if there is a match with formula (sum of score for each position will be equal to 1):
 * 1 / ((ln(maxPositions) + 0.5) * position)
 *
 * Assumption: max position is 10.
 *
 * 3 main cases:
 * 1. no suggestions -> 0 score;
 * 2. original keyword is not present in suggestions -> 0 score;
 * 3. keyword is present in suggestions -> score depends on position, higher position, higher the score
 *
 * Example for the keyword "iphone 6s".
 * Suggestions from Amazon API: ["iphone 6s screen", "iphone 6s screen protector", "iphone 6s", iphone 6s charger"].
 * Original keyword appears in the list of suggested keywords on position 3, therefore score = 1 / ((ln(10) + 0.5) * 3) = 0.12
 *
 */
public class SuggestionsMatchFactor extends ScoringFactor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuggestionsMatchFactor.class);

    private final int maxPositions;

    public SuggestionsMatchFactor(final int maxPositions, final double weight) {
        super(weight);
        this.maxPositions = maxPositions;
    }

    public double calculateScore(final String keyword, final List<String> suggestions) {
        if (suggestions.isEmpty()) {
            //nothing to check, score = 0
            return 0;
        }

        final int indexInSuggestions = suggestions.indexOf(keyword);
        if (indexInSuggestions < 0) {
            //keyword is not present in suggestions, score = 0
            return 0;
        }

        //just starting counting positions from 1
        final double position = indexInSuggestions + 1;

        final double score = 1.0d / ((Math.log(maxPositions) + 0.5) * position);

        LOGGER.debug("[keyword: {}] [score: {}]", keyword, score);

        return score;
    }


    public double calculateScore(final KeywordInfo keywordInfo) {
        return calculateScore(keywordInfo.getKeyword(), keywordInfo.getKeywordSuggestions());
    }
}
