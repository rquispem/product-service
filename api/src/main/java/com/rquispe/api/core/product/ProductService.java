package com.rquispe.api.core.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductService {

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param productId
     * @return the product, if found, else null
     */
    @GetMapping(
            value    = "/products/{productId}",
            produces = "application/json")
    Product getProduct(@PathVariable int productId);
}
