package com.estimator.searchvolume;

import com.estimator.searchvolume.algorithm.Algorithm;
import com.estimator.searchvolume.algorithm.factor.ParentSuggestionsMatchFactor;
import com.estimator.searchvolume.algorithm.factor.ScoringFactor;
import com.estimator.searchvolume.amazonapi.AmazonSuggestionsApiClient;
import com.estimator.searchvolume.endpoint.EstimationEndpoint;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.path;
import static spark.Spark.port;

/**
 * Entry point of the application.
 *
 * 1. initializes web server server
 * 2. sets up endpoint routes
 * 3. handles start/stop of the application
 * 4. initializes all the beans
 *
 */
public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public void run() {

        LOGGER.info("starting application");

        final Config config = ConfigFactory.load();

        final Algorithm algorithm = new Algorithm(config.getConfig("algorithm"));

        final AmazonSuggestionsApiClient amazonSuggestionsApiClient = new AmazonSuggestionsApiClient();

        final EstimationEndpoint estimationEndpoint = new EstimationEndpoint(algorithm, amazonSuggestionsApiClient);

        port(config.getInt("server.http.port"));

        path("/estimate", estimationEndpoint);

        Spark.awaitInitialization();

        LOGGER.info("application started");

    }

    public void stop() {
        LOGGER.info("stopping application");
        Spark.stop();
        LOGGER.info("application stopped");
    }

    public static void main(final String[] args) {

        final App app = new App();

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        app.run();
    }
}
