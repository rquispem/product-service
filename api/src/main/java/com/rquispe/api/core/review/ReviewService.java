package com.rquispe.api.core.review;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ReviewService {
    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/reviews \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"reviewId":456,"author":"me","subject":"yada, yada, yada","content":"yada, yada, yada"}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/reviews",
            consumes = "application/json",
            produces = "application/json")
    Review createReview(@RequestBody Review body);

    /**
     * Sample usage: curl $HOST:$PORT/reviews?productId=1
     *
     * @param productId
     * @return
     */
    @GetMapping(
            value    = "/reviews",
            produces = "application/json")
    List<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/reviews?productId=1
     *
     * @param productId
     */
    @DeleteMapping(value = "/reviews")
    void deleteReviews(@RequestParam(value = "productId", required = true)  int productId);
}
