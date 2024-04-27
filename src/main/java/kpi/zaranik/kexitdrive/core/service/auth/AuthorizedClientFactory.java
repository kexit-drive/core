package kpi.zaranik.kexitdrive.core.service.auth;

import static kpi.zaranik.kexitdrive.core.misc.Constants.GOOGLE_CLIENT_REGISTRATION_ID;

import org.springframework.security.oauth2.client.AuthorizationCodeOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.DelegatingOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.RefreshTokenOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorizedClientFactory {

    private final AuthorizedClientServiceOAuth2AuthorizedClientManager manager;

    public AuthorizedClientFactory(OAuth2AuthorizedClientService authorizedClientService, ClientRegistrationRepository clientRegistrationRepository) {
        this.manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
        this.manager.setAuthorizedClientProvider(new DelegatingOAuth2AuthorizedClientProvider(
            new AuthorizationCodeOAuth2AuthorizedClientProvider(),
            new RefreshTokenOAuth2AuthorizedClientProvider()
        ));
    }

    public OAuth2AuthorizedClient getClientByExternalUserId(String userExternalId) {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId(GOOGLE_CLIENT_REGISTRATION_ID).principal(userExternalId).build();
        return manager.authorize(request);
    }

}
