package com.course.client.controllers;

import ch.qos.logback.classic.Level;
import com.course.client.beans.CartBean;
import com.course.client.beans.CartItemBean;
import com.course.client.beans.ProductBean;
import com.course.client.proxies.MsCartProxy;
import com.course.client.proxies.MsProductProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Controller
public class ClientController {

    @Autowired
    private MsProductProxy msProductProxy;

    @Autowired
    private MsCartProxy msCartProxy;



    @RequestMapping("/")
    public String index(Model model, @RequestParam(value = "cartId", required = false) Long cartId) {

        List<ProductBean> products =  msProductProxy.list();
        model.addAttribute("products", products);

        System.out.println("Beginning Cart Id: "+ cartId);
        int cartSize;

        // Test si Cart Existant
        if (cartId != null){
            // Affichage Nombre Produit Panier
            Optional<CartBean> cartBeanExisting = msCartProxy.getCart(cartId);

            if (cartBeanExisting.get().getProducts().size() == 0){
                cartSize = 0;
            }
            else{
                cartSize = cartBeanExisting.get().getProducts().size() + 1;
            }
            System.out.println("Cart Existant : " + cartBeanExisting.get());
            model.addAttribute("cartSize", cartSize);
            model.addAttribute("cart", cartBeanExisting.get());
        }
        else {
            // Affichage Panier Vide
            cartSize = 0;
            model.addAttribute("cartSize", cartSize);
            // Création d'un Panier de Session
            ResponseEntity<CartBean> cartBeanInstance = msCartProxy.createNewCart();
            model.addAttribute("cart", cartBeanInstance.getBody());
            System.out.println("Cart Créé :" + cartBeanInstance.getBody());
        }
        return "index";
    }

    @RequestMapping("/product-detail/{id}")
    public String  productDetail(Model model,@PathVariable Long id, @RequestParam(value = "cartId") Long cartId){


        Optional<ProductBean> productBeanInstance = msProductProxy.get(id);
        model.addAttribute("product",productBeanInstance.get() );

        System.out.println("Cart Id: "+ cartId);
        int cartSize;

        Optional<CartBean> cartBeanExisting = msCartProxy.getCart(cartId);

        if (cartBeanExisting.get().getProducts().size() == 0){
            cartSize = 0;
        }
        else{
            cartSize = cartBeanExisting.get().getProducts().size() + 1;
        }

        model.addAttribute("cartSize", cartSize);
        model.addAttribute("cart", cartBeanExisting.get());


        return "product";
    }

    @PostMapping
    public void createCart(@RequestBody CartBean cartData){

        ResponseEntity<CartBean> cartBean = msCartProxy.createNewCart();

    }

    @RequestMapping("/add-product/{productId}")
    public String addProduct (Model model, @PathVariable Long productId, @RequestParam(value = "cartId") Long cartId){

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

        // Redirection Accueil
        List<ProductBean> products =  msProductProxy.list();
        model.addAttribute("products", products);

        int cartSize = cart.get().getProducts().size() + 1;
        model.addAttribute("cartSize", cartSize);
        System.out.println("Nombre Produit Panier: " + cartSize);

        //Ajout Cart au variable de Session
        model.addAttribute("cart", cart.get());

        return "redirect:/";
    }
}
