package com.rquispe.api.core.product;

import org.springframework.web.bind.annotation.*;

public interface ProductService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/products \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"name":"product 123","weight":123}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/products",
            consumes = "application/json",
            produces = "application/json")
    Product createProduct(@RequestBody Product body);


    /**
     * Sample usage: curl $HOST:$PORT/products/1
     *
     * @param productId
     * @return the product, if found, else null
     */
    @GetMapping(
            value    = "/products/{productId}",
            produces = "application/json")
    Product getProduct(@PathVariable int productId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/product/1
     *
     * @param productId
     */
    @DeleteMapping(value = "/products/{productId}")
    void deleteProduct(@PathVariable int productId);
}
