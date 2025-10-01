package com.mixo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mixo.model.Product;
import com.mixo.service.NbfcService;
import com.mixo.service.ProductService;
import com.mixo.utils.ProductDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ProductContoller {

	@Autowired
	NbfcService nbfcService;

	@Autowired
	ProductService productService;

	@RequestMapping("/product")
	public String addNewProduct(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		model.addAttribute("product", new Product());
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		return "product";
	}

	@PostMapping("/addProduct")
	public String addUser(@ModelAttribute("product") Product product, BindingResult result, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();
		if (result.hasErrors()) {
			model.addAttribute("product", product);
			model.addAttribute("message", result.getAllErrors());

			model.addAttribute("product", new Product());
			model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
			return "product";
		}

		String message = productService.saveProduct(product, userName); // Save product through the service layer
		model.addAttribute("message", message);
		model.addAttribute("product", product);
		model.addAttribute("nbfcList", nbfcService.getAllNbfcRequestDto(auth));
		return "product"; // Redirect to the user list page after successful save
	}

	@RequestMapping("/productList")
	public String productList(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();

		model.addAttribute("productList", productService.getAllProducts(userName));
		return "productList";
	}

	@GetMapping("/getProducts")
	public ResponseEntity<List<ProductDto>> getProducts(@RequestParam String uid) {
		// Simulate fetching products for the given partner UID
		List<ProductDto> products = productService.getProducts(uid);

		return ResponseEntity.ok(products);
	}

	@RequestMapping("/editProduct")
	public String editProduct(@ModelAttribute("id") Long id, Model model) {
		Product product = productService.getProductById(id);
		model.addAttribute("product", product);
		return "editProduct";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userName = auth.getName();
		String message = productService.updateProduct(product, userName);
		model.addAttribute("message", message);
		return "editProduct";
	}

}
