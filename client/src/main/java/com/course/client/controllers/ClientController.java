package com.course.client.controllers;

import ch.qos.logback.classic.Level;
import com.course.client.beans.*;
import com.course.client.proxies.MsCartProxy;
import com.course.client.proxies.MsOrderProxy;
import com.course.client.proxies.MsProductProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Controller
public class ClientController {

    @Autowired
    private MsProductProxy msProductProxy;

    @Autowired
    private MsCartProxy msCartProxy;

    @Autowired
    private MsOrderProxy msOrderProxy;



    @RequestMapping("/")
    public String createCart(Model model) {

        int cartSize;

        // Affichage Panier Vide
        cartSize = 0;
        model.addAttribute("cartSize", cartSize);
        // Création d'un Panier de Session
        ResponseEntity<CartBean> cartBeanInstance = msCartProxy.createNewCart();
        model.addAttribute("cart", cartBeanInstance.getBody());
        System.out.println("Cart Créé :" + cartBeanInstance.getBody());

        //Création Order de Session
        ResponseEntity<OrderBean> orderBeanInstance = msOrderProxy.createNewOrder();
        model.addAttribute("order", orderBeanInstance.getBody());
        System.out.println("Order Créé :" + orderBeanInstance.getBody());


        return "redirect:/index/cart/" + cartBeanInstance.getBody().getId();
    }

    @RequestMapping("/index/cart/{cartId}")
    public String Index (Model model,@PathVariable Long cartId){
        int cartSize;
        System.out.println("Beginning Cart Id: "+ cartId);
        Optional<CartBean> cartBeanExisting = msCartProxy.getCart(cartId);

        if (cartBeanExisting.get().getProducts().size() == 0){
            cartSize = 0;
        }
        else{
            cartSize = cartBeanExisting.get().getProducts().size() ;
        }
        System.out.println("Cart Existant : " + cartBeanExisting.get());
        model.addAttribute("cartSize", cartSize);
        model.addAttribute("cart", cartBeanExisting.get());

        List<ProductBean> products =  msProductProxy.list();
        model.addAttribute("products", products);

        return "index";
    }



    @RequestMapping("/cart/{cartId}/product-detail/{id}")
    public String  productDetail(Model model,@PathVariable Long id, @PathVariable Long cartId){


        Optional<ProductBean> productBeanInstance = msProductProxy.get(id);
        model.addAttribute("product",productBeanInstance.get() );

        System.out.println("Cart Id: "+ cartId);
        int cartSize;

        Optional<CartBean> cartBeanExisting = msCartProxy.getCart(cartId);

        if (cartBeanExisting.get().getProducts().size() == 0){
            cartSize = 0;
        }
        else{
            cartSize = cartBeanExisting.get().getProducts().size();
        }

        model.addAttribute("cartSize", cartSize);
        model.addAttribute("cart", cartBeanExisting.get());


        return "product";
    }



    @RequestMapping("/cart/{cartId}/add-product/{productId}")
    public String addProduct (Model model, @PathVariable Long productId, @PathVariable Long cartId){

        // id Cart
        System.out.println("Cart Id: "+ cartId);
        System.out.println("productBean Id = " + productId);

        // Récuperer Cart et vérifier exitsance cart
        Optional<CartBean> cart =  msCartProxy.getCart(cartId);

        if (! cart.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't get cart");

        // Création CartItem
        CartItemBean cartItemBean= new CartItemBean();
        cartItemBean.setProductId(productId);
        cartItemBean.setQuantity(1);

        // Ajout CartItem to Cart
        msCartProxy.addProductToCart(cartId,cartItemBean);

        // Récupérer Order et vérifier Existence Order
        Optional <OrderBean> order = msOrderProxy.getOrder(cartId);

        if (! order.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't get order");

        // Création OrderItem
        OrderItemBean orderItemBean = new OrderItemBean();
        orderItemBean.setProductId(productId);
        orderItemBean.setQuantity(1);

        // Ajout OrderItem to Order
        msOrderProxy.addOrderItemToOrder(cartId, orderItemBean);

        // Redirection Accueil
        List<ProductBean> products =  msProductProxy.list();
        model.addAttribute("products", products);

        int cartSize;
        if (cart.get().getProducts().size() == 0){
            cartSize = 0;
        }
        else{
            cartSize = cart.get().getProducts().size() ;
        }
        model.addAttribute("cartSize", cartSize);
        System.out.println("Nombre Produit Panier: " + cartSize);

        //Ajout Cart au variable de Session
        model.addAttribute("cart", cart.get());

        return "redirect:/index/cart/" + cart.get().getId();
    }

    @RequestMapping("/cart/{cartId}/detailsCart")
    public String addProduct (Model model, @PathVariable Long cartId){
        Optional<CartBean> cart =  msCartProxy.getCart(cartId);
        Optional<OrderBean> order = msOrderProxy.getOrder(cartId);
        int cartSize;
        if (cart.get().getProducts().size() == 0){
            cartSize = 0;
        }
        else{
            cartSize = cart.get().getProducts().size() ;
        }
        model.addAttribute("cartSize", cartSize);
        System.out.println("Nombre Produit Panier: " + cartSize);

        //Ajout Cart au variable de Session
        model.addAttribute("cart", cart.get());
        model.addAttribute("order", order.get());
        return "cart";
    }

    @PostMapping("/placeOrder/{idOrder}/{id}")
    public String placeOrder(Model model, @PathVariable Long idOrder,@PathVariable Long id){

        Optional<OrderBean> orderBeanInstance = msOrderProxy.getOrder(idOrder);
        Optional<CartBean> cartBeanInstance = msCartProxy.getCart(id);
        cartBeanInstance.get().removeCart();
        model.addAttribute("cart",cartBeanInstance.get() );
        model.addAttribute("order",orderBeanInstance.get());
        return "index";
    }

}
