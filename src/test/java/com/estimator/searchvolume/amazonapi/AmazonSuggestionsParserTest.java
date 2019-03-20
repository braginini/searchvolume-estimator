package com.estimator.searchvolume.amazonapi;

import com.estimator.searchvolume.amazonapi.exception.AmazonSuggestionsParserException;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
public class AmazonSuggestionsParserTest {

    private AmazonSuggestionsParser tested = new AmazonSuggestionsParser();

    @DataProvider
    public static Object[][] brokenResponseProvider() {
        return new Object[][] {
                {
                    "malformed json"
                },

                {
                    //empty root doc
                    "[]"
                },

                {
                    //broken sequence of elements
                    "[[\"suggestion_1, \"suggestion_2\"], \"some keyword\",[],[]]"
                }
        };
    }

    @DataProvider
    public Object[][] jsonResponseProvider() throws IOException {
        return new Object[][]{

                {
                    //normal case
                    IOUtils.toString(this.getClass().getResource("/amazon/real/cotton bath.json"), Charset.forName("utf-8")),
                        new String[]{"cotton bath mat","cotton bathrobes for women","cotton bath towels","cotton bath mats for bathroom","cotton bathrobe","cotton bath rug","cotton bathrobe men","cotton bathrobe plus size","cotton bath towels clearance prime","cotton bathrobe toddler"}
                },

                {
                        //no suggestions returned
                        IOUtils.toString(this.getClass().getResource("/amazon/real/no suggestions.json"), Charset.forName("utf-8")),
                        new String[]{}
                },

                {
                        //less than 10 suggestions
                        IOUtils.toString(this.getClass().getResource("/amazon/fake/less than 10.json"), Charset.forName("utf-8")),
                        new String[]{"1", "2"}
                }

                //todo add case for not matching original keyword
        };
    }

    @Test(dataProvider = "jsonResponseProvider")
    public void shouldParseAmazonResponse(final String jsonResponse, final String[] expectedSuggestions) {
        //given

        //when
        final List<String> parsedSuggestions = tested.parse(jsonResponse);

        //then
        assertThat(parsedSuggestions).hasSize(expectedSuggestions.length);
        assertThat(parsedSuggestions).containsExactly(expectedSuggestions);
    }

    @Test(dataProvider = "brokenResponseProvider",  expectedExceptions = AmazonSuggestionsParserException.class)
    public void shouldThrowParsingExceptionOnParsingError(final String jsonResponse) {
        //given broken response

        //when
        final List<String> parsedSuggestions = tested.parse(jsonResponse);

        //then AmazonSuggestionsParserException is thrown
    }
}
