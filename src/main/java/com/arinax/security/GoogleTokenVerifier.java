package com.arinax.security;

import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GoogleTokenVerifier {

	 private static final String CLIENT_ID = "189934431159-h41lhbv65a9qt69cr26cikhcgsuh5k5o.apps.googleusercontent.com";
	 private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	    public static GoogleIdToken.Payload verify(String idTokenString) {
	        try {
	            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
	                    new NetHttpTransport(), JSON_FACTORY)
	                    .setAudience(Collections.singletonList(CLIENT_ID))
	                    .build();

	            GoogleIdToken idToken = verifier.verify(idTokenString);
	            if (idToken != null) {
	                return idToken.getPayload();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
}
