package com.estimator.searchvolume;

import com.estimator.searchvolume.amazonapi.AmazonSuggestionsParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public class Utils {

    private static final AmazonSuggestionsParser parser = new AmazonSuggestionsParser();

    public static List<String> readFromResources(final String resource) throws IOException {
        final InputStream resourceAsStream = Utils.class.getResourceAsStream(resource);
        return parser.parse(IOUtils.toString(resourceAsStream, Charset.forName("utf-8")));

    }
}
