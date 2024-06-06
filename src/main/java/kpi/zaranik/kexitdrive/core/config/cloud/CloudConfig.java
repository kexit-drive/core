package kpi.zaranik.kexitdrive.core.config.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableDiscoveryClient
public class CloudConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder(@Value("${spring.servlet.multipart.max-file-size}") DataSize maxInMemorySize) {
        return WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(Math.toIntExact(maxInMemorySize.toBytes())));
    }

}
