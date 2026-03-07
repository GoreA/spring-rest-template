package guru.spring.springresttemplate.client;

//import com.fasterxml.jackson.databind.JsonNode;
import guru.spring.springresttemplate.model.BeerDTO;
import guru.spring.springresttemplate.model.BeerStyle;
import guru.spring.springresttemplate.model.RestPageImpl;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

  private final RestTemplateBuilder restTemplateBuilder;
  private static final String BEERS_URL = "/api/v1/beer";
  private static final String BEER_BY_ID_URL = "/api/v1/beer/{id}";

  @Override
  public Page<BeerDTO> listBeers() {
    return this.listBeers(null, null, null, null, null);
  }

  @Override
  public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
                                 Integer pageSize) {
    RestTemplate restTemplate = restTemplateBuilder.build();
//    ResponseEntity<Map> mapResponse =
//        restTemplate.getForEntity(BEERS_URL, Map.class);
//
//    ResponseEntity<JsonNode> jsonResponse =
//        restTemplate.getForEntity(BEERS_URL, JsonNode.class);
//
//    jsonResponse.getBody().findPath("content")
//        .forEach(jsonNode -> {
//          System.out.println(jsonNode.get("beerName").asText());
//        });

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(BEERS_URL);

    ResponseEntity<RestPageImpl> pageResponse = restTemplate
        .getForEntity(uriComponentsBuilder.toUriString(), RestPageImpl.class);

    if (beerName != null) {
      uriComponentsBuilder.queryParam("beerName", beerName);
    }

    if (beerStyle != null) {
      uriComponentsBuilder.queryParam("beerStyle", beerStyle);
    }

    if (showInventory != null) {
      uriComponentsBuilder.queryParam("showInventory", beerStyle);
    }

    if (pageNumber != null) {
      uriComponentsBuilder.queryParam("pageNumber", beerStyle);
    }

    if (pageSize != null) {
      uriComponentsBuilder.queryParam("pageSize", beerStyle);
    }

    return pageResponse.getBody();
  }

  @Override
  public Page<BeerDTO> listBeersByName(String beerName) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(BEERS_URL);

    if (beerName != null) {
      uriComponentsBuilder.queryParam("beerName", beerName);
    }

    ResponseEntity<RestPageImpl> pageResponse = restTemplate
        .getForEntity(uriComponentsBuilder.toUriString(), RestPageImpl.class);

    return pageResponse.getBody();
  }

  @Override
  public BeerDTO getBeerById(UUID id) {

    RestTemplate restTemplate = restTemplateBuilder.build();
    return restTemplate.getForObject(BEER_BY_ID_URL, BeerDTO.class, id);

  }

  @Override
  public BeerDTO createBeer(BeerDTO beerDTO) {
    RestTemplate restTemplate = restTemplateBuilder.build();
//    ResponseEntity<BeerDTO> beerResponse = restTemplate
//        .postForEntity(BEERS_URL, beerDTO, BeerDTO.class);
    URI uri = restTemplate.postForLocation(BEERS_URL, beerDTO);
    return restTemplate.getForObject(uri.getPath(), BeerDTO.class);
  }

  @Override
  public BeerDTO updateBeer(UUID id, BeerDTO newDto) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    restTemplate.put(BEER_BY_ID_URL, newDto, id);
    return getBeerById(id);
  }

  @Override
  public void deleteBeer(UUID id) {
    RestTemplate restTemplate = restTemplateBuilder.build();
    restTemplate.delete(BEER_BY_ID_URL, id);
  }
}
