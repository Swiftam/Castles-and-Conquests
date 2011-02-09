package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.commons.codec.binary.*;

import play.libs.WS;

import com.google.gson.*;

public class Facebook {
	private String _appId;
	private String _sigReq;
	private String _secret;
	private static final String SIGN_ALGORITHM = "HMACSHA256";
	
	public Facebook(String appId, String secret, String signed_request) {
		_appId = appId;
		_sigReq = signed_request;
		_secret = secret;
	}
	
	public String getAppId() {
		return _appId;
	}

	public String getUserId()
		throws FacebookRequestException {
		int idx = _sigReq.indexOf(".");
		byte[] sig = new Base64(true).decode(_sigReq.substring(0,idx).getBytes());
		String rawpayload = _sigReq.substring(idx+1);
		String payload = new String(new Base64(true).decode(rawpayload));
		
		FacebookRequest fbObj = new Gson().fromJson(payload, FacebookRequest.class);
		
		if ( !fbObj.getAlgorithm().equals("HMAC-SHA256") ) {
			throw new FacebookRequestException("Unexpected hash algorithm: " + fbObj.getAlgorithm() );
		}
		
		try {
			this.checkSignature(rawpayload, sig);
		} catch ( FacebookRequestException e ) {
			throw e;
		}
		
		return fbObj.getUserId();
	}
	
	protected Boolean checkSignature(String rawpayload, byte[] sig )
		throws FacebookRequestException {
		try {
			SecretKeySpec key = new SecretKeySpec(_secret.getBytes(), SIGN_ALGORITHM );
			Mac mac = Mac.getInstance(SIGN_ALGORITHM);
			mac.init(key);
			byte [] mysig = mac.doFinal(rawpayload.getBytes());
			
			if ( !Arrays.equals(mysig, sig ) ) {
				throw new FacebookRequestException("Non-matching signature for request");
			}
		} catch ( NoSuchAlgorithmException e ) {
			throw new FacebookRequestException("Unknown hash algoritm: " + SIGN_ALGORITHM, e );
		} catch ( InvalidKeyException e ) {
			throw new FacebookRequestException("Wrong key for " + SIGN_ALGORITHM, e );
		}
		return true;
	}
	
	protected String getUrl(String name) {
		return getUrl( name, "" );
	}
	
	protected String getUrl(String name, String path ) {
		return getUrl( name, path, null );
	}

	 /**
	   * Build the URL for given domain alias, path and parameters.
	   *
	   * @param $name String the name of the domain
	   * @param $path String optional path (without a leading slash)
	   * @param $params Array optional query parameters
	   * @return String the URL for the given parameters
	   */
	  protected String getUrl(String name, String path, Map<String, String> params ) {
	    String url = "";
	    if ( name.equals( "api") ) {
	    	url = "https://api.facebook.com/";
	    } else if ( name.equals( "api_read" ) ) {
	    	url = "https://api-read.facebook.com/";
	    } else if ( name.equals( "graph" ) ) {
	    	url = "https://graph.facebook.com/";
	    } else if ( name.equals( "www" ) ) {
	    	url = "https://www.facebook.com/";
	    }

	    if ( !path.isEmpty() ) {
	    	if ( path.charAt(0) == '/' ) {
	    		path = path.substring(1);
    		}
	    	url += path;
	    }
	    
	    if ( null != params && !params.isEmpty() ) {
	    	url += "?";
	    	int count = 0;
	    	for ( String key : params.keySet() ) {
	    		if ( count > 0 ) url += "&";
	    		url += WS.encode(key);
	    		url += "=";
	    		url += WS.encode(params.get(key)); 
    			count++;
	    	}
	    }

	    return url;
	  }
	  
	  public String getAuthorizeUrl(String appUrl) {
		  return "https://www.facebook.com/dialog/oauth?client_id="
		  	+ getAppId()
		  	+ "&redirect_uri="
		  	+ appUrl;
     }
	  
	public String getLoginUrl(String currentUrl, int fbconnect, int canvas) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("api_key", getAppId() );
		params.put("cancel_url", currentUrl );
		params.put("canvas", String.valueOf(canvas) );
		params.put("display", "page" );
		params.put("fbconnect", String.valueOf(fbconnect) );
		params.put("next", currentUrl );
		params.put("return_session", "1");
		params.put("session_version", "3" );
		params.put("v", "1.0");
	    return getUrl("www", "login.php", params );
	  }
}
