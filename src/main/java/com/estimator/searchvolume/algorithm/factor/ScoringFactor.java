package com.estimator.searchvolume.algorithm.factor;

import com.estimator.searchvolume.algorithm.domain.KeywordInfo;

/**
 * Interface that each scoring factor must implement to be used in {@link com.estimator.searchvolume.algorithm.Algorithm}
 *
 */
public abstract class ScoringFactor {

    private final double weight;

    protected ScoringFactor(final double weight) {
        this.weight = weight;
    }

    /**
     * Calculates score in a range of [0; 1] for given {@link KeywordInfo}
     *
     * @param keywordInfo {@link KeywordInfo} used in scoring
     * @return double representation of a score in a range of [0; 1]
     */
    public abstract double calculateScore(final KeywordInfo keywordInfo);

    public double weight() {
        return weight;
    }
}
