package io.github.greennlab.javageneric;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.greennlab.javageneric.support.spring.Dataset;
import io.github.greennlab.javageneric.support.spring.DatasetArgumentsResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new DatasetArgumentsResolver(objectMapper()));
  }

  @Bean
  ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

}
