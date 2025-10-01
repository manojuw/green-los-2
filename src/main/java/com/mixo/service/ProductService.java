package com.mixo.service;

import java.util.List;

import com.mixo.model.Product;
import com.mixo.utils.ProductDto;

public interface ProductService {

	String saveProduct(Product product, String userName);

	List<Product> getAllProducts(String userName);

	List<ProductDto> getProducts(String uid);

	Product getProductByProductId(String productId);

	Product getProductByProductIdAndUid(String productId, String uid);

	String updateProduct(Product product, String userName);

	Product getProductById(Long id);

}
