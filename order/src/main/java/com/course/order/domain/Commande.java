package com.course.order.domain;


import javax.persistence.*;
import java.util.List;

@Entity
public class Commande {
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orders;
    private Double total = 00.0;

    public Commande() {
    }

    public Commande(Long id, List<OrderItem> orders, Double total) {
        this.id = id;
        this.orders = orders;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrderItem> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public void totalsOrder(){
        total = 0.0;
        for (OrderItem order: orders){
            total += order.getPrice()*order.getQuantity();
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orders=" + orders +
                '}';
    }
    public void addOrderItem ( OrderItem orderItem ){
        this.orders.add(orderItem);
        this.total += orderItem.getPrice()* orderItem.getQuantity();
    }
}
