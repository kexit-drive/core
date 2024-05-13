package kpi.zaranik.kexitdrive.core.service.user;

import java.security.Principal;
import java.util.Optional;
import kpi.zaranik.kexitdrive.core.dto.security.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public TokenResponse getAccessToken(OAuth2AuthorizedClient authorizedClient) {
        return new TokenResponse(authorizedClient.getAccessToken().getTokenValue());
    }

    public Optional<String> getCurrentUserExternalId() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(Principal::getName);
    }

}
