package guru.spring.springresttemplate.client;


import guru.spring.springresttemplate.config.RestTemplateConfig;
import guru.spring.springresttemplate.model.BeerDTO;
import guru.spring.springresttemplate.model.BeerStyle;
import guru.spring.springresttemplate.model.RestPageImpl;
import java.net.URI;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withAccepted;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
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

  BeerDTO dto;
  String dtoJson;

  @BeforeEach
  void setUp() {
    RestTemplate restTemplate = restTemplateBuilder.build();
    server = MockRestServiceServer.bindTo(restTemplate).build();
    when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
    beerClient = new BeerClientImpl(mockRestTemplateBuilder);
    dto = getBeerDto();
    dtoJson = objectMapper.writeValueAsString(dto);
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

    mockGetOperation();

    BeerDTO beerById = beerClient.getBeerById(dto.getId());
    assertThat(beerById.getId()).isEqualByComparingTo(dto.getId());
  }

  @Test
  void testCreateBeer() {
    URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.BEER_BY_ID_URL)
        .build(dto.getId());

    server.expect(method(HttpMethod.POST))
        .andExpect(requestTo(URL +
            BeerClientImpl.BEERS_URL))
        .andRespond(withAccepted().location(uri));

    mockGetOperation();

    BeerDTO responseDto = beerClient.createBeer(dto);
    assertThat(responseDto.getId()).isEqualTo(dto.getId());
  }

  @Test
  void testUpdateBeer() {
    URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.BEER_BY_ID_URL)
        .build(dto.getId());

    dto.setBeerName("New Name");
    dtoJson = objectMapper.writeValueAsString(dto);

    server.expect(method(HttpMethod.PUT))
        .andExpect(requestToUriTemplate(URL +
            BeerClientImpl.BEER_BY_ID_URL, dto.getId()))
        .andRespond(withNoContent());

    mockGetOperation();

    BeerDTO responseDto = beerClient.updateBeer(dto.getId(), dto);
    assertThat(responseDto.getId()).isEqualTo(dto.getId());
    assertThat(responseDto.getBeerName()).isEqualTo(dto.getBeerName());
  }

  @Test
  void testDeleteBeer() {
    server.expect(method(HttpMethod.DELETE))
        .andExpect(requestToUriTemplate(URL +
            BeerClientImpl.BEER_BY_ID_URL, dto.getId()))
        .andRespond(withNoContent());

    beerClient.deleteBeer(dto.getId());

    server.verify();
  }

  @Test
  void testDeleteNotFound() {
    server.expect(method(HttpMethod.DELETE))
        .andExpect(requestToUriTemplate(URL +
            BeerClientImpl.BEER_BY_ID_URL, dto.getId()))
        .andRespond(withResourceNotFound());

    assertThrows(HttpClientErrorException.class, () -> {
          beerClient.deleteBeer(dto.getId());
        });
    server.verify();
  }

  @Test
  void testListBeersWthQueryParams() {
    String payload = objectMapper.writeValueAsString(getPage());

    server.expect(method(HttpMethod.GET))
        .andExpect(requestToUriTemplate(URL + BeerClientImpl.BEERS_URL +
            "?beerName={beerName}&beerStyle={beerStyle}", "Mango Bobs", "IPA"))
        .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

    Page<BeerDTO> dtos = beerClient.listBeers("Mango Bobs", BeerStyle.IPA, null, null, null);
    assertThat(dtos.getContent().size()).isGreaterThan(0);
  }

  private void mockGetOperation() {
    server.expect(method(HttpMethod.GET))
        .andExpect(requestToUriTemplate(URL +
            BeerClientImpl.BEER_BY_ID_URL, dto.getId()))
        .andRespond(withSuccess(dtoJson, MediaType.APPLICATION_JSON));
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
