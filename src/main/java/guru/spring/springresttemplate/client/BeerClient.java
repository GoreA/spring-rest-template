package guru.spring.springresttemplate.client;

import guru.spring.springresttemplate.model.BeerDTO;
import guru.spring.springresttemplate.model.BeerStyle;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface BeerClient {

  Page<BeerDTO> listBeers();

  Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber,
                          Integer pageSize);

  Page<BeerDTO> listBeersByName(String beerName);

  BeerDTO getBeerById(UUID id);

  BeerDTO createBeer(BeerDTO beerDTO);

  BeerDTO updateBeer(UUID id, BeerDTO newDto);

  void deleteBeer(UUID id);
}
