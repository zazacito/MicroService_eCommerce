package com.course.order.controllers;


import com.course.order.domain.Commande;
import com.course.order.domain.OrderItem;
import com.course.order.repositories.OrderItemRepository;
import com.course.order.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;

@RestController
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;


    @PostMapping(value = "/order")
    public ResponseEntity<Commande> createNewOrder()
    {
        Commande commande = orderRepository.save(new Commande());

        if (commande == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't create a new order");

        return new ResponseEntity<>(commande, HttpStatus.CREATED);


    }

    @GetMapping(value = "/order/{id}")
    public Optional<Commande> getOrder(@PathVariable Long id){

        Optional<Commande> order = orderRepository.findById(id);

        if (order == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Couldn't get order");
        return order;

    }


    @PostMapping(value = "/order/{id}")
    @Transactional
    public ResponseEntity<OrderItem> addOrderItemToOrder(@PathVariable Long id, @RequestBody OrderItem orderItem){

        Commande commande = orderRepository.getOne(id);

        if (commande == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't get cart");

        commande.addOrderItem(orderItem);

        orderRepository.save(commande);

        return new ResponseEntity<OrderItem>(orderItem, HttpStatus.CREATED);

    }


}
