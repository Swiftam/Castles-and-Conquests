package utils;

public class FacebookRequestException extends Exception {
	FacebookRequestException(String message) {
		super(message);
	}
	
	FacebookRequestException(String message, Exception innerException ) {
		super(message, innerException);
	}
}
