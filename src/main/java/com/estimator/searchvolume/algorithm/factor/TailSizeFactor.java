package com.estimator.searchvolume.algorithm.factor;

import com.estimator.searchvolume.algorithm.domain.KeywordInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TailSizeFactor - size of the tail of the keyword (number of words)
 * Example for keyword "iphone x screen". Tail size is equal to 3.
 *
 * Shorter the tail the better.
 *
 * //TODO Improvements:
 * 1. add unit test
 *
 */
public class TailSizeFactor extends ScoringFactor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TailSizeFactor.class);

    public TailSizeFactor(final double weight) {
        super(weight);
    }

    @Override
    public double calculateScore(final KeywordInfo keywordInfo) {

        final double score = 1.0d / keywordInfo.getTailSize();

        LOGGER.debug("[keyword: {}] [score: {}]", keywordInfo.getKeyword(), score);

        return score;
    }
}
