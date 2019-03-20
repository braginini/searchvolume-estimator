package com.estimator.searchvolume.algorithm;

import com.estimator.searchvolume.algorithm.domain.KeywordInfo;
import com.estimator.searchvolume.algorithm.domain.KeywordInfoBuilder;
import com.estimator.searchvolume.algorithm.factor.*;
import com.estimator.searchvolume.amazonapi.AmazonSuggestionsApiClient;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Class that holds algorithm of estimation:
 * <p>
 * 1. creates weighted {@link ScoringFactor}s
 * 2. builds a chain of factors
 * 3. evaluates {@link KeywordInfo} to calculate the score [0.0; 1.0]
 * <p>
 * //TODO Improvements:
 * 1. resulting score might be > 1.0, normalize this with math
 * 2. make scoring factor chain configurable instead of fixed.
 * 3. add unit test
 */
public class Algorithm {

    private static final Logger LOGGER = LoggerFactory.getLogger(Algorithm.class);

    private final List<ScoringFactor> factorChain;

    Algorithm(final List<ScoringFactor> factorChain) {
        this.factorChain = factorChain;

        //check integrity of weights
        final double sum = factorChain.stream().mapToDouble(ScoringFactor::weight).sum();
        if (sum != 1.0d) {
            throw new IllegalStateException("scoring factor weights sum is not equal to 1 [sum: " + sum + "] check factorWeights");
        }
    }

    public Algorithm(final Config algorithmConfig) {
        this(chain(algorithmConfig));
    }

    /**
     * Builds {@link ScoringFactor} chain using algorithm config [factorsWeights, maxSuggestions]
     *
     * @param algorithmConfig
     * @return
     */
    private static List<ScoringFactor> chain(final Config algorithmConfig) {
        final List<ScoringFactor> chain = new ArrayList<>();

        final Config factorsWeights = algorithmConfig.getConfig("factorsWeights");
        final int maxSuggestions = algorithmConfig.getInt("maxSuggestions");

        final SuggestionsMatchFactor suggestionsMatchFactor =
                new SuggestionsMatchFactor(maxSuggestions, factorsWeights.getDouble(SuggestionsMatchFactor.class.getSimpleName()));
        final ParentSuggestionsMatchFactor parentSuggestionsMatchFactor =
                new ParentSuggestionsMatchFactor(factorsWeights.getDouble(ParentSuggestionsMatchFactor.class.getSimpleName()),
                        suggestionsMatchFactor);
        final SuggestionsNumberFactor suggestionsNumberFactor =
                new SuggestionsNumberFactor(maxSuggestions, factorsWeights.getDouble(SuggestionsNumberFactor.class.getSimpleName()));
        final TailSizeFactor tailSizeFactor =
                new TailSizeFactor(factorsWeights.getDouble(TailSizeFactor.class.getSimpleName()));

        chain.add(parentSuggestionsMatchFactor);
        chain.add(suggestionsMatchFactor);
        chain.add(suggestionsNumberFactor);
        chain.add(tailSizeFactor);

        return chain;
    }

    /**
     * Goes through a chain of factors summing up results and applying factor weighs.
     *
     * @param keywordInfo
     * @return score in a range [0.0, 1.0]
     */
    public double evaluate(final KeywordInfo keywordInfo) {

        LOGGER.debug("evaluating [keyword: {}] on factor chain", keywordInfo.getKeyword());

        final double score = factorChain.stream().map(f -> f.calculateScore(keywordInfo) * f.weight())
                .mapToDouble(Double::doubleValue).sum();

        //todo ugly but works for now, improve
        final double normalizedScore = score > 1.0d ? 1.0d : score;

        LOGGER.debug("evaluated  [keyword: {}] on factor chain with [score: {}]", keywordInfo.getKeyword(), normalizedScore);

        return normalizedScore;

    }
}
