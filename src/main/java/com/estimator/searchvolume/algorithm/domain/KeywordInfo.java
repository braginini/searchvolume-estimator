package com.estimator.searchvolume.algorithm.domain;

import java.util.List;
import java.util.Optional;

/**
 * Representation of input for the {@link com.estimator.searchvolume.algorithm.Algorithm}
 */
public class KeywordInfo {

    /**
     * Keyword literal
     * Examples: "iphone 6s", "iphone 6s screen replacement"
     */
    private final String keyword;

    /**
     * List of words that make the  {@link this#keyword} Order of words is preserved!!!
     * Example for keyword "iphone 6s screen replacement": ["iphone", "6s", "screen", "replacement"]
     */
    private final List<String> words;

    /**
     * Size of the tail of the keywords - number of words in the keyword.
     */
    private final int tailSize;

    /**
     * List of keyword literals that were suggested by Amazon suggestion API.
     * Order of the suggestions is preserved!!!.
     * 0 - top suggestion
     * 10 - lowest suggestion
     */
    private final List<String> keywordSuggestions;

    /**
     * A {@link KeywordInfo} which is a parent (by prefix) for the original one
     * Example: for keyword "iphone 6s screen replacement" keyword "iphone 6s screen" is a parent.
     *
     * Can be absent.
     *
     */
    private final Optional<KeywordInfo> parentKeyword;

    public KeywordInfo(final String keyword, final List<String> words, final int tailSize, final List<String> keywordSuggestions,
            final Optional<KeywordInfo> parentKeyword) {
        this.keyword = keyword;
        this.words = words;
        this.tailSize = tailSize;
        this.keywordSuggestions = keywordSuggestions;
        this.parentKeyword = parentKeyword;
    }

    public int getTailSize() {
        return tailSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<String> getWords() {
        return words;
    }

    public List<String> getKeywordSuggestions() {
        return keywordSuggestions;
    }

    public Optional<KeywordInfo> getParentKeyword() {
        return parentKeyword;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
