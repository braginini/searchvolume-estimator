package com.estimator.searchvolume.algorithm.factor;

import com.estimator.searchvolume.algorithm.domain.KeywordInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ParentSuggestionsMatchFactor - original keyword appears in suggestions for the keyword that is his parent.
 * The scoring logic checks whether keyword appears in suggestions for the keywords that are prefixes for the original one.
 * <p>
 * Example for the keyword "iphone x screen protector". This keyword appears in suggestions for the keywords "iphone x screen",
 * "iphone x" up to "iphone" which are prefixes for the original one.
 *  This means that such a keyword is quite "hot".
 */
public class ParentSuggestionsMatchFactor extends ScoringFactor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParentSuggestionsMatchFactor.class);

    private final SuggestionsMatchFactor suggestionsMatchFactor;

    public ParentSuggestionsMatchFactor(final double weight, final SuggestionsMatchFactor suggestionsMatchFactor) {
        super(weight);
        this.suggestionsMatchFactor = suggestionsMatchFactor;
    }

    public double calculateScore(final KeywordInfo keywordInfo) {

        //iterate over parents and check if keyword appears in suggestions for them
        //logic starts from itself
        double score = 0;
        int iteration = 0;
        KeywordInfo currentInfo = keywordInfo;
        while (currentInfo != null) {

            //final double iterationWeight = 1 / ((Math.log(tailSize) + 0.5) * (keywordInfo.getTailSize() - iteration));
            final double iterationWeight = 1.0d;

            final double iterationScore = iterationWeight * suggestionsMatchFactor.calculateScore(keywordInfo.getKeyword(),
                    currentInfo.getKeywordSuggestions());

            score += iterationScore;

            LOGGER.trace("[keyword: {}] [iteration: {}] [iterationWeight: {}] [iterationScore: {}] [score: {}]", keywordInfo.getKeyword(),
                    iteration, iterationWeight, iterationScore, score);

            currentInfo = currentInfo.getParentKeyword().orElse(null);
            iteration++;
        }

        LOGGER.debug("[keyword: {}] [score: {}]", keywordInfo.getKeyword(), score);

        return score;
    }
}
