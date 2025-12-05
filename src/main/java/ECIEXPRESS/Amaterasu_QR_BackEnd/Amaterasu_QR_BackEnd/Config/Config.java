package ECIEXPRESS.Amaterasu_QR_BackEnd.Amaterasu_QR_BackEnd.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class Config {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
