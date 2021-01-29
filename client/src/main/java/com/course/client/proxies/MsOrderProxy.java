package com.course.client.proxies;

import com.course.client.beans.CartItemBean;
import com.course.client.beans.OrderBean;
import com.course.client.beans.OrderItemBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "ms-order", url = "localhost:8093")
public interface MsOrderProxy {

    @PostMapping(value ="/order")
    public ResponseEntity<OrderBean> createNewOrder();

    @GetMapping(value="/order/{id}")
    public Optional<OrderBean> getOrder(@PathVariable Long id);

    @PostMapping(value = "/cart/{id}")
    public ResponseEntity<OrderItemBean> addOrderItemToOrder (@PathVariable Long id, @RequestBody OrderItemBean orderItem);
}
