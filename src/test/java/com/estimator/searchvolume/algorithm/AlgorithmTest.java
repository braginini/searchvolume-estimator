package com.estimator.searchvolume.algorithm;

import com.estimator.searchvolume.algorithm.domain.KeywordInfo;
import com.estimator.searchvolume.algorithm.factor.TailSizeFactor;
import com.typesafe.config.Config;
import org.assertj.core.util.Lists;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AlgorithmTest {

    @Test(expectedExceptions = IllegalStateException.class,
            expectedExceptionsMessageRegExp = "scoring factor weights sum is not equal to 1.*")
    public void shouldFailOnFactorWeightsIntegrityCheck() {
        //given weights not summing up into 1
        final Config factorWeightsConfig = mock(Config.class);
        when(factorWeightsConfig.getDouble(any())).thenReturn(999d);
        final Config algorithmConfig = mock(Config.class);
        when(algorithmConfig.getConfig(eq("factorsWeights"))).thenReturn(factorWeightsConfig);

        //when
        new Algorithm(algorithmConfig);

        //then IllegalStateException is thrown
    }

    @Test
    public void shouldEvaluateKeyword() {
        //given
        final Config factorWeightsConfig = mock(Config.class);
        when(factorWeightsConfig.getDouble(any())).thenReturn(0.25d);
        final Config algorithmConfig = mock(Config.class);
        when(algorithmConfig.getConfig(eq("factorsWeights"))).thenReturn(factorWeightsConfig);
        when(algorithmConfig.getInt(eq("maxSuggestions"))).thenReturn(10);

        final Algorithm algorithm = new Algorithm(algorithmConfig);
        final KeywordInfo keywordInfo =
                new KeywordInfo("1 2", Lists.newArrayList("1", "2"), 2,
                        Lists.newArrayList("1", "2", "3"), Optional.empty());

        //when
        final double score = algorithm.evaluate(keywordInfo);

        //then
        assertThat(score).isLessThanOrEqualTo(1d);
    }

    @Test
    public void shouldNormalizeScoreWhenMoreThanOne() {
        //given factors that return score > 1
        final TailSizeFactor tailSizeFactor = mock(TailSizeFactor.class);
        when(tailSizeFactor.weight()).thenReturn(1.0d);
        when(tailSizeFactor.calculateScore(any(KeywordInfo.class))).thenReturn(999d);

        final Algorithm algorithm = new Algorithm(Collections.singletonList(tailSizeFactor));

        //when
        final double score = algorithm.evaluate(mock(KeywordInfo.class));

        //then
        assertThat(score).isLessThanOrEqualTo(1d);
    }
}
