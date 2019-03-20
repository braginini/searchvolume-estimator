package com.estimator.searchvolume.amazonapi;

import com.estimator.searchvolume.amazonapi.exception.AmazonSuggestionsParserException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Parses Amazon's suggestions API JSON response extracting suggested keywords.
 * Parser preserves the order of the suggested keywords.
 *
 * According to multiple test requests to the API the following was discovered:
 * 1. response is JSON
 * 2. root document is JSON Array
 * 3. first element of the root document is the original requested keyword and is always present
 * 4. original keyword in response might not match requested one (e.g. keyword "@")
 * 5. second element of the root document is the list of suggestions
 * 6. sequence of root document elements is fixed, all elements returned as empty if not present (not null)
 *
 * See example responses in test resources amazonapi/real
 *
 * Improvements:
 * 1. do we need only suggestions in result of parsing? Or maybe more data, e.g. original keyword, nodes, etc
 * 2. add check for item 4 above
 * 3. map raw json response instead of accessing elements by index
 * 4. clarify what are the other elements returned
 *
 */
public class AmazonSuggestionsParser {

    private final ObjectMapper mapper;

    //visible for testing
    AmazonSuggestionsParser(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public AmazonSuggestionsParser() {
        this(new ObjectMapper());
    }

    /**
     * Parses raw JSON response of Amazon's suggestions API.
     *
     * @param suggestionsJson raw API response
     * @return a string list of suggested keywords
     */
    public List<String> parse(final String suggestionsJson) {
        try {
            final List responseRoot = mapper.readValue(suggestionsJson, new TypeReference<List>(){});
            //final String originalKeyword = (String) responseRoot.get(0);
            final List<String> suggestions = (List<String>) responseRoot.get(1);
            return suggestions;
        } catch (Exception e) {
            throw new AmazonSuggestionsParserException("error parsing suggestionsJson: '" + suggestionsJson + "'", e);
        }
    }


}
