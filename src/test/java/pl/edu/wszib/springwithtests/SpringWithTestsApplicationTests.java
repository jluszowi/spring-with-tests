package pl.edu.wszib.springwithtests;

import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.wszib.springwithtests.dao.ProductDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketItemDao;
import pl.edu.wszib.springwithtests.dto.ProductDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketDTO;
import pl.edu.wszib.springwithtests.model.Product;
import pl.edu.wszib.springwithtests.model.ShoppingBasket;
import pl.edu.wszib.springwithtests.model.ShoppingBasketItem;
import pl.edu.wszib.springwithtests.model.Vat;
import pl.edu.wszib.springwithtests.service.ShoppingBasketService;

@RunWith(SpringRunner.class)
@SpringBootTest

public class SpringWithTestsApplicationTests {

	@Autowired
	ShoppingBasketService service;

	@Autowired
	ShoppingBasketDao shoppingBasketDao;

	@Autowired
	ProductDao productDao;

	@Autowired
	ShoppingBasketItemDao shoppingBasketItemDao;

	@Autowired
	Mapper mapper;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();


	//Test Spring 1
	@Test
	public void testShoppingBasketIdNoExit () {
		int testBasketId = 3124;
		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(1);
		productDTO.setVat(Vat.VALUE_23);
		productDTO.setCost(57d);
		productDTO.setName("testproduct");
		expectedException.expect(NotFoundException.class);
		service.addProduct(testBasketId, productDTO);
	}

	//Test Spring 2
	@Test
	public void testShoppingBasketExistProductNotExistt () {
		ShoppingBasket shoppingBasket = new ShoppingBasket();
		shoppingBasket = shoppingBasketDao.save(shoppingBasket);

		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(1);
		productDTO.setVat(Vat.VALUE_23);
		productDTO.setCost(57d);
		productDTO.setName("testproduct");
		expectedException.expect(NotFoundException.class);
		service.addProduct(shoppingBasket.getId(), productDTO);
	}

	//Test Spring 3
	@Test
	public void testShoppingBasketExisProductExistShoppingBasketItemExist() {
		ShoppingBasket shoppingBasket = new ShoppingBasket();
		shoppingBasket = shoppingBasketDao.save(shoppingBasket);

		Product product = new Product();
		product.setVat(Vat.VALUE_8);
		product.setCost(2124d);
		product.setName("dfsdsgag");
		product = productDao.save(product);

		ShoppingBasketItem shoppingBasketItem = new ShoppingBasketItem();
		shoppingBasketItem.setProduct(product);
		shoppingBasketItem.setShoppingBasket(shoppingBasket);
		shoppingBasketItem.setAmount(1);
		shoppingBasketItem = shoppingBasketItemDao.save(shoppingBasketItem);

		ShoppingBasketDTO result = service.addProduct(shoppingBasket.getId(),
				mapper.map(product, ProductDTO.class));

		Assert.assertEquals(shoppingBasket.getId(), result.getId());
		Assert.assertEquals(1, result.getItems().size());
		final Product copyProduct = product;
		Assert.assertTrue(result.getItems()
				.stream()
				.anyMatch(i -> i.getProduct().getId()
						.equals(copyProduct.getId())));
		final  ShoppingBasketItem copyShoppingBascetItem = shoppingBasketItem;
		Assert.assertTrue(result.getItems()
				.stream()
				.filter(i -> i.getProduct().getId()
						.equals(copyProduct.getId()))
				.findFirst()
				.map(i -> i.getAmount() == copyShoppingBascetItem.getAmount() + 1)
				.orElse(false));

	}

	//Test Spring 3
	@Test
	public void testShoppingBasketExisProductExistShoppingBasketItemNotExist() {
		ShoppingBasket shoppingBasket = new ShoppingBasket();
		shoppingBasket = shoppingBasketDao.save(shoppingBasket);

		Product product = new Product();
		product.setVat(Vat.VALUE_8);
		product.setCost(2124d);
		product.setName("dfsdsgag");
		product = productDao.save(product);



		ShoppingBasketDTO result = service.addProduct(shoppingBasket.getId(),
				mapper.map(product, ProductDTO.class));

		Assert.assertEquals(shoppingBasket.getId(), result.getId());
		Assert.assertEquals(1, result.getItems().size());
		final Product copyProduct = product;
		Assert.assertTrue(result.getItems()
				.stream()
				.anyMatch(i -> i.getProduct().getId()
						.equals(copyProduct.getId())));

		Assert.assertTrue(result.getItems()
				.stream()
				.filter(i -> i.getProduct().getId()
						.equals(copyProduct.getId()))
				.findFirst()
				.map(i -> i.getAmount() == 1)
				.orElse(false));

		Assert.assertNotNull(
				shoppingBasketItemDao
				.findByProductIdAndShoppingBasketId
						(product.getId(), shoppingBasket.getId()));



	}


}
