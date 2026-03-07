package guru.spring.springresttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.restclient.autoconfigure.RestTemplateBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

  @Value("${rest.template.url}")
  String baseUrl;

  @Bean
  RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
    RestTemplateBuilder builder = configurer.configure(new RestTemplateBuilder());
    DefaultUriBuilderFactory uriBuilderFactory = new
        DefaultUriBuilderFactory(baseUrl);

    return builder.uriTemplateHandler(uriBuilderFactory);
  }
}
