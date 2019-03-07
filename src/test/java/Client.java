package test.java.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import test.java.pojo.Budget;
import test.java.pojo.ErrorResponse;

public class Client {
	RestTemplate restTemplate;
	ObjectMapper objectMapper;

	public Client() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
	}

	public ErrorResponse getError(String url) {
		ErrorResponse errorResponse = null;
		String responseBody = null;
		HttpStatus statusCode = null;
		try {
			restTemplate.getForEntity(url, ErrorResponse.class);
		} catch (HttpClientErrorException e) {
			responseBody = e.getResponseBodyAsString(); 
			statusCode = e.getStatusCode();
		}
		try {
			if (responseBody != null) {
				errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class); 
				errorResponse.setStatusCode(statusCode);
			}
		} catch (IOException e) {

		}
		return errorResponse;
	}

	public ResponseEntity<Budget> getBudget(String url) {
		return restTemplate.getForEntity(url, Budget.class);
	}
}