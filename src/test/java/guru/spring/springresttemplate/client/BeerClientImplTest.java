package guru.spring.springresttemplate.client;

import static org.junit.jupiter.api.Assertions.*;

import guru.spring.springresttemplate.model.BeerDTO;
import guru.spring.springresttemplate.model.BeerStyle;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
class BeerClientImplTest {

  @Autowired
  BeerClientImpl beerClient;

  @Test
  void listBeers() {
    beerClient.listBeers();
  }

  @Test
  void listBeersByName() {
    beerClient.listBeersByName("IPA");
  }

  @Test
  void getBeerById() {
    Page<BeerDTO> beerDTOS = beerClient.listBeers();

    ObjectMapper mapper = new ObjectMapper();
    BeerDTO beerDTO = mapper.convertValue(beerDTOS.getContent().get(0), BeerDTO.class);
    beerClient.getBeerById(beerDTO.getId());
    assertNotNull(beerDTO);
  }

  @Test
  void testCreateBeer() {

    BeerDTO newDto = BeerDTO.builder()
        .price(new BigDecimal("10.99"))
        .beerName("Mango Bobs")
        .beerStyle(BeerStyle.IPA)
        .quantityOnHand(500)
        .upc("123245")
        .build();

    BeerDTO savedDto = beerClient.createBeer(newDto);
    assertNotNull(savedDto);
  }

  @Test
  void testUpdateBeer() {

    BeerDTO newDto = BeerDTO.builder()
        .price(new BigDecimal("10.99"))
        .beerName("Mango Bobs 5.0")
        .beerStyle(BeerStyle.IPA)
        .quantityOnHand(500)
        .upc("123245")
        .build();

    BeerDTO savedDto = beerClient.createBeer(newDto);
    final String newName = "Mango Bobs 6.0";
    newDto.setBeerName(newName);
    BeerDTO updatedBeer = beerClient.updateBeer(savedDto.getId(), newDto);
    assertEquals(updatedBeer.getBeerName(), newName);
  }

  @Test
  void testDeleteBeer() {
    BeerDTO newDto = BeerDTO.builder()
        .price(new BigDecimal("10.99"))
        .beerName("Mango Bobs 2.0")
        .beerStyle(BeerStyle.IPA)
        .quantityOnHand(500)
        .upc("123245")
        .build();

    BeerDTO savedDto = beerClient.createBeer(newDto);

    beerClient.deleteBeer(savedDto.getId());

    assertThrows(HttpClientErrorException.class, () -> {
      beerClient.getBeerById(savedDto.getId());
    });
  }
}