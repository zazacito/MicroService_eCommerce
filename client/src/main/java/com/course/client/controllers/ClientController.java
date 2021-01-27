package com.course.client.controllers;

import com.course.client.beans.ProductBean;
import com.course.client.proxies.MsProductProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class ClientController {

    @Autowired
    private MsProductProxy msProductProxy;

    @RequestMapping("/")
    public String index(Model model) {

        List<ProductBean> products =  msProductProxy.list();

        model.addAttribute("products", products);

        return "index";
    }

    @RequestMapping("/product-detail/{id}")
    public String  productDetail(Model model,@PathVariable Long id){

        Optional<ProductBean> productBeanInstance = msProductProxy.get(id);

        model.addAttribute("product",productBeanInstance.get() );

        return "product";
    }

}
