package kpi.zaranik.kexitdrive.core.config.googleclient;

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

@Configuration
public class GoogleWebClientConfig {

    @Value("${google-apis-url}")
    private String googleApisUrl;

    @Bean
    public GoogleApiClient googleApiCLient(@Qualifier("servletContextAuthorizedWebClient") WebClient servletContextAuthorizedWebClient) {
        var factory = HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(servletContextAuthorizedWebClient))
            .build();
        return factory.createClient(GoogleApiClient.class);
    }

    @Bean
    public WebClient servletContextAuthorizedWebClient(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientRepository authorizedClientRepository
    ) {
        var exchangeFilterFunction = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository, authorizedClientRepository);
        return WebClient.builder()
            .baseUrl(googleApisUrl)
            .apply(exchangeFilterFunction.oauth2Configuration())
            .build();
    }

    @Bean
    public WebClient fileImportingWebClient(@Value("${web-client-max-in-memory-size-in-megabytes}") long maxInMemorySizeMB) {
        int maxInMemorySizeInBytes = Math.toIntExact(DataSize.ofMegabytes(maxInMemorySizeMB).toBytes());
        return WebClient.builder()
            .baseUrl(googleApisUrl)
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeInBytes))
            .build();
    }

}
