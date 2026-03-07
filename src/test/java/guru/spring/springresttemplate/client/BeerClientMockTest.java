package guru.spring.springresttemplate.client;


import guru.spring.springresttemplate.config.RestTemplateConfig;
import guru.spring.springresttemplate.model.BeerDTO;
import guru.spring.springresttemplate.model.BeerStyle;
import guru.spring.springresttemplate.model.RestPageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
@Import(RestTemplateConfig.class)
@ExtendWith(MockitoExtension.class)
public class BeerClientMockTest {

  static final String URL = "http://localhost:8080";

  BeerClient beerClient;

  MockRestServiceServer server;

  @Autowired
  RestTemplateBuilder restTemplateBuilder;

  @Autowired
  ObjectMapper objectMapper;

  @Mock
  RestTemplateBuilder mockRestTemplateBuilder;

  @BeforeEach
  void setUp() {
    RestTemplate restTemplate = restTemplateBuilder.build();
    server = MockRestServiceServer.bindTo(restTemplate).build();
    when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
    beerClient = new BeerClientImpl(mockRestTemplateBuilder);
  }

  @Test
  void testListBeers() {
    String payload = objectMapper.writeValueAsString(getPage());

    server.expect(method(HttpMethod.GET))
        .andExpect(requestTo(URL + BeerClientImpl.BEERS_URL))
        .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

    Page<BeerDTO> dtos = beerClient.listBeers();
    assertThat(dtos.getContent().size()).isGreaterThan(0);
  }

  @Test
  void testGetBeerById() {
    BeerDTO dto = getBeerDto();
    String payload = objectMapper.writeValueAsString(dto);

    server.expect(method(HttpMethod.GET))
        .andExpect(requestToUriTemplate(URL +
            BeerClientImpl.BEER_BY_ID_URL, dto.getId()))
        .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

    BeerDTO beerById = beerClient.getBeerById(dto.getId());
    assertThat(beerById.getId()).isEqualByComparingTo(dto.getId());
  }

  BeerDTO getBeerDto(){
    return BeerDTO.builder()
        .id(UUID.randomUUID())
        .price(new BigDecimal("10.99"))
        .beerName("Mango Bobs")
        .beerStyle(BeerStyle.IPA)
        .quantityOnHand(500)
        .upc("123245")
        .build();
  }

  RestPageImpl getPage(){
    return new RestPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
  }
}
