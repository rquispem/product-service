package com.rquispe.microservices.composite.product;

import com.rquispe.api.composite.product.ProductAggregate;
import com.rquispe.api.composite.product.RecommendationSummary;
import com.rquispe.api.composite.product.ReviewSummary;
import com.rquispe.api.core.product.Product;
import com.rquispe.api.core.recommendation.Recommendation;
import com.rquispe.api.core.review.Review;
import com.rquispe.microservices.composite.product.services.ProductCompositeIntegration;
import com.rquispe.util.exceptions.InvalidInputException;
import com.rquispe.util.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

//In each Spring integration test class, we configure TestSecurityConfig to override
// the existing security configuration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = {ProductCompositeServiceApplication.class, TestSecurityConfig.class},
		properties = {"spring.main.allow-bean-definition-overriding=true", "eureka.client.enaled=false", "spring.cloud.config.enabled=false"})
class ProductCompositeServiceApplicationTests {

	private static final int PRODUCT_ID_OK = 1;
	private static final int PRODUCT_ID_NOT_FOUND = 2;
	private static final int PRODUCT_ID_INVALID = 3;

	@Autowired
	private WebTestClient client;

	@MockBean
	private ProductCompositeIntegration compositeIntegration;

	@BeforeEach
	public void setUp() {

		when(compositeIntegration.getProduct(PRODUCT_ID_OK)).
				thenReturn(Mono.just(new Product(PRODUCT_ID_OK, "name", 1, "mock-address")));
		when(compositeIntegration.getRecommendations(PRODUCT_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address"))));
		when(compositeIntegration.getReviews(PRODUCT_ID_OK)).
				thenReturn(Flux.fromIterable(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address"))));

		when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

		when(compositeIntegration.getProduct(PRODUCT_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
	}

	@Test
	public void contextLoads() {
	}

	@Disabled
	@Test
	public void createCompositeProduct2() {
		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
				singletonList(new RecommendationSummary(1, "a", 1, "c")),
				singletonList(new ReviewSummary(1, "a", "s", "c")), null);

		postAndVerifyProduct(compositeProduct, OK);
	}

	@Disabled
	@Test
	public void deleteCompositeProduct() {
		ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1,
				singletonList(new RecommendationSummary(1, "a", 1, "c")),
				singletonList(new ReviewSummary(1, "a", "s", "c")), null);

		postAndVerifyProduct(compositeProduct, OK);

		deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
		deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
	}

	@Test
	public void getProductById() {

		client.get()
				.uri("/product-composite/" + PRODUCT_ID_OK)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
				.jsonPath("$.recommendations.length()").isEqualTo(1)
				.jsonPath("$.reviews.length()").isEqualTo(1);
	}

	@Test
	public void getProductNotFound() {

		client.get()
				.uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
				.jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
	}

	@Test
	public void getProductInvalidInput() {

		client.get()
				.uri("/product-composite/" + PRODUCT_ID_INVALID)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody()
				.jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
				.jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
	}

	private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
		client.post()
				.uri("/product-composite")
				.body(just(compositeProduct), ProductAggregate.class)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		client.delete()
				.uri("/product-composite/" + productId)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus);
	}

}
