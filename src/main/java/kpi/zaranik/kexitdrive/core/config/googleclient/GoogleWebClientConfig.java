package kpi.zaranik.kexitdrive.core.config.googleclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GoogleWebClientConfig {

    @Bean
    public WebClient fileImportingWebClient(
        @Value("${spring.servlet.multipart.max-file-size}") DataSize maxInMemorySize,
        @Value("${google-apis-url}") String googleApisUrl
    ) {
        int maxInMemorySizeInBytes = Math.toIntExact(maxInMemorySize.toBytes());
        return WebClient.builder()
            .baseUrl(googleApisUrl)
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxInMemorySizeInBytes))
            .build();
    }

}
