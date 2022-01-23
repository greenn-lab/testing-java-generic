package io.github.greennlab.javageneric.support.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.servlet.ServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class DatasetArgumentsResolver implements HandlerMethodArgumentResolver {

  private final ObjectMapper objectMapper;


  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(Dataset.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws IOException {

    final String datasetName = deduceDatasetName(parameter);

    final Object value = containsRequestBody(datasetName, webRequest)
        .orElseThrow(() -> new IllegalArgumentException("not matched by key: " + datasetName));

    if (value instanceof Collection) {
      final Class<?> generic = extractGenericTypeInCollection(parameter.getGenericParameterType());
      if (null != generic) {
        return Arrays.asList(
            ((Collection<?>) value).stream().map(
                    (Function<Object, Object>) o -> objectMapper.convertValue(o, generic))
                .toArray());
      }
    }

    return value;
  }

  private Optional<Object> containsRequestBody(String datasetName, NativeWebRequest webRequest)
      throws IOException {
    final ServletRequest request = Objects.requireNonNull(
        webRequest.getNativeRequest(ServletRequest.class));

    @SuppressWarnings("rawtypes") final Map map = objectMapper.readValue(request.getInputStream(),
        Map.class);

    return Optional.ofNullable(map.get(datasetName));
  }

  @NonNull
  private String deduceDatasetName(MethodParameter parameter) {
    final Dataset dataset =
        Objects.requireNonNull(parameter.getParameterAnnotation(Dataset.class));

    if (!ObjectUtils.isEmpty(dataset.value())) {
      return dataset.value();
    }

    final Class<?> parameterType = parameter.getParameterType();

    if (Collection.class.isAssignableFrom(parameterType)) {
      final Class<?> generic = extractGenericTypeInCollection(parameter.getGenericParameterType());

      if (null != generic) {
        return Dataset.PREFIX
            + generic.getSimpleName().substring(0, 1).toLowerCase()
            + generic.getSimpleName().substring(1);
      }
    }

    return Objects.requireNonNull(parameter.getParameterName());
  }

  private Class<?> extractGenericTypeInCollection(Type type) {
    if (type instanceof ParameterizedType) {

      final Type[] generics = ((ParameterizedType) type).getActualTypeArguments();

      if (!ObjectUtils.isEmpty(generics) && generics[0] instanceof Class) {
        return (Class<?>) generics[0];
      }
    }

    return null;
  }
}
