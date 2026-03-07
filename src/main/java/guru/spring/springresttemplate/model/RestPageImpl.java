package guru.spring.springresttemplate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@JsonIgnoreProperties(ignoreUnknown = true, value = "pageable")
public class RestPageImpl<BeerDTO> extends PageImpl<BeerDTO> {

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public RestPageImpl(@JsonProperty("content") List<BeerDTO> content,
                      @JsonProperty("number") int number,
                      @JsonProperty("size") int size,
                      @JsonProperty("totalElements") long totalElements) {
    super(content, PageRequest.of(number, size), totalElements);
  }

  public RestPageImpl(List<BeerDTO> content, Pageable pageable, long totalElements) {
    super(content, pageable, totalElements);
  }
}
