package test.java.pojo;

import org.springframework.http.HttpStatus;

public class ErrorResponse {
	private String error;
	private HttpStatus statusCode;

	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public HttpStatus getStatusCode() {
		return this.statusCode;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}
}