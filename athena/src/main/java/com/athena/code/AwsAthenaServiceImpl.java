package com.athena.code;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest;
import software.amazon.awssdk.services.athena.model.GetQueryResultsResponse;
import software.amazon.awssdk.services.athena.model.InvalidRequestException;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.QueryExecutionState;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionRequest;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;

/**
 * Service implementation for executing queries on Amazon Athena.
 * 
 * <p>
 * This service provides methods to start query execution, check the query status,
 * retrieve query results, and execute a query end-to-end with proper error handling
 * and logging.
 * </p>
 * 
 * Dependencies like {@link AthenaClient}, {@link QueryExecutionContext}, and
 * {@link ResultConfiguration} are injected via constructor.
 * 
 * @author MAC development team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AwsAthenaServiceImpl implements AwsAthenaService{
    
    private final AthenaClient athenaClient;

    private final QueryExecutionContext queryExecutionContext;

    private final ResultConfiguration resultConfiguration;
 
      /**
     * Starts the execution of the given SQL query in Athena.
     * 
     * @param query The SQL query string to execute.
     * @return The unique query execution ID assigned by Athena.
     * @throws SdkException If there is an error calling the AWS SDK.
     */
    private String startQueryExecution(String query) {
        StartQueryExecutionRequest request = StartQueryExecutionRequest.builder()
                .queryString(query)
                .queryExecutionContext(queryExecutionContext)
                .resultConfiguration(resultConfiguration)
                .build();
 
        StartQueryExecutionResponse response = athenaClient.startQueryExecution(request);
        return response.queryExecutionId();
    }
 
     /**
     * Retrieves the current status of a query execution by its ID.
     * 
     * @param queryExecutionId The Athena query execution ID.
     * @return The response containing the query execution status and metadata.
     * @throws SdkException If there is an error calling the AWS SDK.
     */
    private GetQueryExecutionResponse getQueryExecutionStatus(String queryExecutionId) {
        GetQueryExecutionRequest request = GetQueryExecutionRequest.builder().queryExecutionId(queryExecutionId).build();
        return athenaClient.getQueryExecution(request);
    }
 
     /**
     * Retrieves the results of a completed query execution.
     * 
     * @param queryExecutionId The Athena query execution ID.
     * @return The response containing the query results.
     * @throws SdkException If there is an error calling the AWS SDK.
     */
    private GetQueryResultsResponse getQueryResults(String queryExecutionId) {
        GetQueryResultsRequest request = GetQueryResultsRequest.builder().queryExecutionId(queryExecutionId).build();
        return athenaClient.getQueryResults(request);
    }

    /**
     * Executes the given SQL query in Athena and returns the results as a string.
     * <p>
     * This method handles the full lifecycle of a query including starting the query,
     * polling for completion, error handling, and retrieving results.
     * </p>
     * 
     * @param query The SQL query string to execute.
     * @return The query results as a string, or null if the query failed or an error occurred.
     */
    @Override
    public String executeQuery(String query) {
        String queryExecutionId;
        try {

            queryExecutionId = startQueryExecution(query);
            log.debug("Query started with Execution ID: {}", queryExecutionId);
            GetQueryExecutionResponse queryExecution;
            do {
                Thread.sleep(1000);
                queryExecution = getQueryExecutionStatus(queryExecutionId);
                QueryExecutionState state = queryExecution.queryExecution().status().state();
                if (state == QueryExecutionState.FAILED || state == QueryExecutionState.CANCELLED) {
                    String errorMessage = queryExecution.queryExecution().status().athenaError() != null
                            ? queryExecution.queryExecution().status().athenaError().errorMessage()
                            : "Unknown error";
                    log.error("Query failed or was cancelled: {}", errorMessage);
                    return null;
                }
            } while (queryExecution.queryExecution().status().state() == QueryExecutionState.RUNNING);
    
            GetQueryResultsResponse queryResults = getQueryResults(queryExecutionId);
            return queryResults.resultSet().toString();
    
        } catch (InvalidRequestException e) {
            log.error("Invalid request to Athena: {}", e.getMessage(), e);
        } catch (SdkException e) {
            log.error("AWS SDK error occurred: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted while waiting for Athena query to complete", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred during Athena query execution", e);
        }
    
        return null;
    }

}
