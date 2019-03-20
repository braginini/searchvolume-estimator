package com.estimator.searchvolume.algorithm.factor;

import com.estimator.searchvolume.algorithm.domain.KeywordInfo;
import com.estimator.searchvolume.algorithm.domain.KeywordInfoBuilder;
import org.assertj.core.data.Offset;
import org.assertj.core.util.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionsMatchFactorTest {

    private final SuggestionsMatchFactor tested = new SuggestionsMatchFactor(10, 1d);

    @Test(dataProvider = "originalKeywordInfoProvider")
    public void shouldCalculateScore(final KeywordInfo originalKeyword, final double expectedScore) {
        //given

        //when
        final double calculatedScore = tested.calculateScore(originalKeyword);

        //then
        assertThat(calculatedScore).isEqualTo(expectedScore, Offset.offset(0.1));
    }

    @DataProvider
    public Object[][] originalKeywordInfoProvider() throws IOException {
        //case 1 - no suggestions -> 0 score
        final ArrayList<String> suggestions_1 = new ArrayList<>();
        final KeywordInfo keywordInfo_1 = new KeywordInfoBuilder().build("no suggestions", suggestions_1, Optional.empty());

        //case 2 - no match in suggestions -> 0 score

        final ArrayList<String> suggestions_2 = Lists.newArrayList("1", "2", "3");
        final KeywordInfo keywordInfo_2 = new KeywordInfoBuilder().build("not a match", suggestions_2, Optional.empty());

        //case 3 - position 1 -> 0.35 score

        final ArrayList<String> suggestions_3 = Lists.newArrayList("1", "2", "3");
        final KeywordInfo keywordInfo_3 = new KeywordInfoBuilder().build("1", suggestions_3, Optional.empty());

        //case 4 - position 10 -> 0.1 score
        final ArrayList<String> suggestions_4 = Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        final KeywordInfo keywordInfo_4 = new KeywordInfoBuilder().build("10", suggestions_4, Optional.empty());

        //case 5 - position 5 -> 0.07 score

        final ArrayList<String> suggestions_5 = Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        final KeywordInfo keywordInfo_5 = new KeywordInfoBuilder().build("5", suggestions_5, Optional.empty());

        return new Object[][]{
                {
                    keywordInfo_1, 0.0d
                },

                {
                    keywordInfo_2, 0.0d
                },

                {
                    keywordInfo_3, 0.35d
                },

                {
                    keywordInfo_4, 0.1d
                },

                {
                    keywordInfo_5, 0.07d
                }
        };
    }
}
