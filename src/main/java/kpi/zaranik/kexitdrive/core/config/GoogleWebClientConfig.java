package kpi.zaranik.kexitdrive.core.config;

import kpi.zaranik.kexitdrive.core.client.GoogleApiClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration(proxyBeanMethods = false)
public class GoogleWebClientConfig {

    private static final int MAX_IN_MEMORY_SIZE_IN_BYTES = Math.toIntExact(DataSize.ofMegabytes(512).toBytes());

    @Bean("authorizedWebClient")
    public WebClient authorizedWebClient(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientRepository authorizedClientRepository,
        @Value("${google-apis-url}") String googleApisUrl
    ) {
        var oauth2Client = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);
        oauth2Client.setDefaultClientRegistrationId("google");
        return WebClient.builder()
            .baseUrl(googleApisUrl)
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE_IN_BYTES))
            .apply(oauth2Client.oauth2Configuration())
            .build();
    }

    @Bean
    public GoogleApiClient googleApiCLient(@Qualifier("authorizedWebClient") WebClient authorizedWebClient) {
        var factory = HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(authorizedWebClient))
            .build();
        return factory.createClient(GoogleApiClient.class);
    }

}
