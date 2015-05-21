package org.fenixedu.cms.domain;

import java.util.Arrays;
import java.util.Collections;

import org.fenixedu.bennu.io.domain.GroupBasedFile;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GoogleAPIService extends GoogleAPIService_Base {

    public GoogleAPIService() {
        super();
    }

    public void delete() {
        setBennu(null);
        for (GoogleAPIConnection conn : getConnectionsSet()) {
            conn.delete();
        }
        deleteDomainObject();
    }

    public GoogleAuthorizationCodeFlow makeFlow() {
        return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                getClientId(), getClientSecret(), Arrays.asList("https://www.googleapis.com/auth/analytics.readonly",
                        "https://www.googleapis.com/auth/analytics.edit")).setAccessType("offline").build();
    }
}
