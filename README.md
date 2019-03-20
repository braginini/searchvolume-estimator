Amazon Keyword Search Volume Estimator
======================================

A keyword search volume estimation service 

Requirements
------------

* Java 8+
* Maven
* Docker

How to
------

1. build the project and create a Docker image:
    ```bash
    mvn clean install
    ```

2. run the service and attach to the container to see the logs:
    ```bash    
    docker run --name amazon-searchvolume-estimator -d -rm -p 8080:8080 amazon-searchvolume-estimator:0.1   
    
    ```
    
    ```bash
    docker logs --tail 100 -f amazon-searchvolume-estimator
    ```

3. test request:
    ```bash
    curl -G 'http://localhost:8080/estimate' --data-urlencode "keyword=iphone xr screen protector"
    ```

4. response:
    ```json
    {"Keyword":"iphone xr screen protector","score":84}
    ```
    
Project structure (package structure)
-------------------------------------

        .
        ├── algorithm   # search volume estimation algorithm related code                  
        │  
        ├── amazonapi   # Ammazon Suggestions API related code (client, parser, etc)                        
        │
        ├── endpoint    # application client interface endpoints      
        │
        └── App.java    # application entry point
    

High level application flow
---------------------------
1. accept client request with the specified keyword;
2. collect parent keywords by prefix (e.g. for `iphone xr screen` parents are `[iphone xr, iphone]`);
3. call Amazon Suggestions API for each of the keywords collected on the previous step and combine into the tree (see example [tree/graph](dag.pdf))
4. evaluate the tree on a chain of weighted scoring factors and sum up the scores; 
5. return response to the client;  

Algorithm
---------

The idea of the [algorithm](src/main/java/com/estimator/searchvolume/algorithm/Algorithm.java) is to combine multiple weighted [scoring factors](src/main/java/com/estimator/searchvolume/algorithm/factor/ScoringFactor.java) into a chain and evaluate [keyword info](src/main/java/com/estimator/searchvolume/algorithm/domain/KeywordInfo.java)  
that contains requested keyword meta data (e.g. keyword literal, suggestions, prefix parents tree).

Scoring factors work like plug-ins that can be enabled/disabled via [application.conf](src/main/resources/application.conf) (weight = 0 disables factor) or new ones can be added.
Existing scoring factors (see details in the java doc of each factor):
* *ParentSuggestionsMatchFactor* - original keyword appears in suggestions for the keyword that is his parent;
* *SuggestionsMatchFactor* - original keyword appears in a lists of suggestions for itself;
* *SuggestionsNumberFactor* - more direct suggestions the better;
* *TailSizeFactor* - size of the tail of the keyword (number of words).

**Note:** the algorithm is not good in estimating search volume for short-tail keywords (e.g. `iphone` or `makita`).
It does much better job with long-tail keywords (however, not with very long ones e.g. `drive the surprising truth about what motivates us`). 

Three possible solutions for that (definitely out the scope and won't fit into 10s SLA):
1. Naive. Make an assumption that short-tail keywords having 10 suggestions are quite popular by default.
2. Use typos correction of Amazon Suggestions API. E.g. if requesting mistyped keyword `iphnoe x` API will return suggestions for `iphone x`.
   This might be an indicator for a popular keyword because Amazon corrected it. So maybe for a keyword `iphone x` we intentionally
   make a typo when requesting API and check if it was corrected.
3. Do brand detection over rankings to detect as much as possible brands keywords and marking them as highly popular.   

Next steps
----------

Next steps are grouped by topic tasks that can be addressed to finish up the service.

**Algorithm:**
* approach short-tail keywords
* consider using Bayesian network. The keyword prefix tree is a Direct Acyclic Graph ([example](dag.pdf))
* approach cases like `hilfiger sweater` that have suggestions without original keyword being prefix ([example](https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=hilfiger%20sweater))
* research if `nodes` field in Amazon Suggestions API can be used;
* review and fix score > 1 issue, make mathematical normalization of scoring factors;
* approach mistyped keywords (typos) in scoring factors.

**CI/CD and monitoring**
* enable travis
* automated deployment (cloudformation, teraform)
* datadog dashboard and monitors (alerts)

**Test**
* acceptance test for SLA integrated in CI/CD pipeline
* review //todo in the code for unit tests

**Chore**
* improve splitting of the keywords by space
* make Amazon Suggestions API calls asynchronous
* review code for //todo improvements
 
**Bottom line**
To sum up the work, I see that solution should focus on probalistic estimation. Collecting a full tree/graph of the suggested keywords making up as much as possible connections to build a neural network.   








