package com.estimator.searchvolume.amazonapi;

import com.estimator.searchvolume.amazonapi.exception.AmazonSuggestionsApiClientException;
import okhttp3.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AmazonSuggestionsApiClientTest {

    private AmazonSuggestionsApiClient tested;
    private AmazonSuggestionsParser parser;
    private OkHttpClient httpClient;

    @BeforeMethod
    public void setup() {
        httpClient = mock(OkHttpClient.class);
        parser = mock(AmazonSuggestionsParser.class);
        tested = new AmazonSuggestionsApiClient(httpClient, parser);
    }

    @Test(expectedExceptions = AmazonSuggestionsApiClientException.class)
    public void shouldThrownClientExceptionOnIOError() throws IOException {
        //given httpClient failing on failingCall
        final Call failingCall = mock(Call.class);
        when(httpClient.newCall(any(Request.class))).thenReturn(failingCall);
        when(failingCall.execute()).thenThrow(new IOException("forced"));

        //when
        tested.lookupSuggestions("keyword");

        //then AmazonSuggestionsApiClient Exception is thrown
    }

    @Test(expectedExceptions = AmazonSuggestionsApiClientException.class)
    public void shouldThrownClientExceptionOnNonOkStatus() throws IOException {
        //given httpClient returning non 200 code when calling
        final Response non200Response = mock(Response.class);
        when(non200Response.code()).thenReturn(500);
        final Call call = mock(Call.class);
        when(httpClient.newCall(any(Request.class))).thenReturn(call);
        when(call.execute()).thenReturn(non200Response);

        //when
        tested.lookupSuggestions("keyword");

        //then AmazonSuggestionsApiClient Exception is thrown
    }

    @Test
    public void shouldNormallyReturnSuggestions() throws IOException {
        //given OK response from the API
        tested = new AmazonSuggestionsApiClient(httpClient, new AmazonSuggestionsParser());
        final String apiResponseBody = "[ \"the man in\", [ \"the man in the high castle\", \"the man in the high castle book\" ], [], [], \"3FG2BP1ITKL4H\"]";
        final ResponseBody okResponseBody = mock(ResponseBody.class);
        when(okResponseBody.string()).thenReturn(apiResponseBody);

        final Response okResponse = mock(Response.class);
        when(okResponse.code()).thenReturn(200);
        when(okResponse.body()).thenReturn(okResponseBody);

        final Call call = mock(Call.class);
        when(call.execute()).thenReturn(okResponse);

        when(httpClient.newCall(any(Request.class))).thenReturn(call);

        //when
        final List<String> suggestions = tested.lookupSuggestions("the man in");

        //then
        assertThat(suggestions).containsExactly("the man in the high castle", "the man in the high castle book");
    }
}
