package com.estimator.searchvolume;

import com.estimator.searchvolume.App;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Actually, an acceptance test that better to run in CI/CD pipeline after deployment for example to test SLAs.
 *
 * Just put it here to have at least some integration test.
 *
 * Method {@link this#shouldEstimateInTime} is very dummy SLA test. More sophisticated one would be:
 * 1. run as acceptance test in CI/CD pipeline
 * 2. estimate average size of N real requests (X)
 * 3. estimate average size of N real responses (Y)
 * 4. ping service's host with X+Y packet size N times to calculate network overhead (t)
 * 5. perform N test requests to endpoint subtracting N*t network overhead time
 * 6. check average execution time
 *
 */
public class IT {

    private App app;

    @BeforeClass
    public void setup() {
        app = new App();
        app.run();
    }

    @AfterClass
    public void tearDown() {
        app.stop();
    }

    @Test(enabled = true)
    public void shouldEstimate() {

        when().
                get("http://localhost:8080/estimate?keyword=iphone x case").
        then().
                statusCode(200).
                body("Keyword", equalTo("iphone x case"),
                        "score", Matchers.greaterThan(0),
                        "score", Matchers.lessThanOrEqualTo(100));

    }

    @Test(enabled = true, timeOut = 12_000L)
    public void shouldEstimateInTime() {
        //given max execution time and start time in ms
        final long maxExecutionTime = 10_000L;
        final long started = System.currentTimeMillis();

        //when
        when().
                get("http://localhost:8080/estimate?keyword=iphone x case").
        then().
                statusCode(200);

        //then
        final long finishedInMs = System.currentTimeMillis() - started;
        assertThat(finishedInMs).isLessThan(maxExecutionTime);

    }
}
