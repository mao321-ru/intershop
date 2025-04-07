package org.example.intershop.config;

import com.example.payclient.ApiClient;
import com.example.payclient.api.PaymentApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PaysrvConfig {

    @Value( "${paysrv.url}")
    private String serverUrl;

    @Bean
    ApiClient apiClient() {
        var apiClient = new ApiClient();
        apiClient.setBasePath( serverUrl);
        return apiClient;
    }

    @Bean
    PaymentApi defaultApi( ApiClient apiClient) {
        return new PaymentApi( apiClient);
    }

}
