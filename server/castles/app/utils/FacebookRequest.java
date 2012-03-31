package utils;

public class FacebookRequest {
	 private String algorithm;
	 private String user_id;
	 
	 public FacebookRequest() {
		 this.algorithm = null;
		 this.user_id = null;
	 }
	 
	 public void setAlgorithm(String algorithm) {
		 this.algorithm = algorithm;
	 }
	 
	 public String getAlgorithm() {
		 return this.algorithm;
	 }
	 
	 public void setUserId(String userId) {
		 this.user_id = userId;
	 }
	 
	 public String getUserId() {
		 return this.user_id;
	 }
}
