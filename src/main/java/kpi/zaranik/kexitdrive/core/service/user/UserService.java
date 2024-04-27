package kpi.zaranik.kexitdrive.core.service.user;

import kpi.zaranik.kexitdrive.core.client.GoogleApiClient;
import kpi.zaranik.kexitdrive.core.dto.security.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final GoogleApiClient client;

    public TokenResponse getAccessToken(OAuth2AuthorizedClient authorizedClient) {
        return new TokenResponse(authorizedClient.getAccessToken().getTokenValue());
    }

}
