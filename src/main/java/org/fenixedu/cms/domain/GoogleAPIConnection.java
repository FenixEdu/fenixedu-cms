package org.fenixedu.cms.domain;

import java.io.IOException;
import java.util.Locale;

import org.fenixedu.bennu.portal.domain.PortalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;

public class GoogleAPIConnection extends GoogleAPIConnection_Base {

    private static Logger logger = LoggerFactory.getLogger(GoogleAPIConnection.class);

    public GoogleAPIConnection() {
        super();
    }

    public void delete() {
        getSite().setAnalyticsCode(null);
        setSite(null);
        setService(null);
        deleteDomainObject();
    }

    public Analytics getAnalytics() {
        GoogleCredential credential =
                new GoogleCredential.Builder().addRefreshListener(new CredentialRefreshListener() {

                    @Override
                    public void onTokenResponse(Credential arg0, TokenResponse arg1) throws IOException {
                        FenixFramework.atomic(() -> {
                            setAccessToken(arg1.getAccessToken());
                            setRefreshToken(arg1.getRefreshToken());
                        });
                    }

                    @Override
                    public void onTokenErrorResponse(Credential arg0, TokenErrorResponse arg1) throws IOException {
                        delete();
                    }
                }).setTransport(new NetHttpTransport()).setJsonFactory(JacksonFactory.getDefaultInstance())
                        .setClientSecrets(getService().getClientId(), getService().getClientSecret()).build()
                        .setAccessToken(getAccessToken()).setRefreshToken(getRefreshToken());

        return new Analytics.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential).setApplicationName(
                PortalConfiguration.getInstance().getApplicationTitle().getContent(Locale.ENGLISH)).build();
    }
}
