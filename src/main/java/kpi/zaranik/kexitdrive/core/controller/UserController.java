package kpi.zaranik.kexitdrive.core.controller;

import kpi.zaranik.kexitdrive.core.config.security.CurrentUser;
import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import kpi.zaranik.kexitdrive.core.dto.security.TokenResponse;
import kpi.zaranik.kexitdrive.core.service.auth.AuthorizedClientFactory;
import kpi.zaranik.kexitdrive.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthorizedClientFactory authorizedClientFactory;

    @GetMapping("current")
    public UserInfo currentUser(@CurrentUser UserInfo user) {
        return user;
    }

    @GetMapping("getAccessToken")
    public TokenResponse getAccessToken(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient) {
        return userService.getAccessToken(authorizedClient);
    }

}
