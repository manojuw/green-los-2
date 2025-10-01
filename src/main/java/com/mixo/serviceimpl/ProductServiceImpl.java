package com.mixo.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mixo.model.Product;
import com.mixo.repository.ProductRepository;
import com.mixo.service.ProductService;
import com.mixo.utils.AlphaNumIdGenerator;
import com.mixo.utils.ProductDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

	@Autowired
	ProductRepository productRepository;

	@Override
	public String saveProduct(Product product, String userName) {
		try {

			product.setProductId(AlphaNumIdGenerator.generateId(12));
			product.setCreatedBy(userName);
			productRepository.save(product);
			return "Product saved successfully";
		} catch (Exception e) {
			log.error("Error saving product : {}", e.getMessage());
			return "Error saving product : " + e.getMessage();
		}
	}

	@Override
	public List<Product> getAllProducts(String userName) {
		return productRepository.findAll();
	}

	@Override
	public List<ProductDto> getProducts(String uid) {
		// TODO Auto-generated method stub

		List<Product> products = productRepository.findByUid(uid);

		List<ProductDto> productDtos = new ArrayList<>();
		if (products.isEmpty()) {
			return productDtos;
		}
		for (Product product : products) {
			ProductDto productDto = new ProductDto();
			BeanUtils.copyProperties(product, productDto);
			productDtos.add(productDto);
		}
		return productDtos;
	}

	@Override
	public Product getProductByProductId(String productId) {
		Optional<Product> product = productRepository.findByProductId(productId);

		if (product.isPresent()) {
			return product.get();
		}
		return null;
	}

	@Override
	public Product getProductByProductIdAndUid(String productId, String uid) {

		Optional<Product> product = productRepository.findByProductIdAndUid(productId, uid);
		if (product.isPresent()) {
			return product.get();
		}
		return null;
	}

	@Override
	public Product getProductById(Long id) {
		return productRepository.findById(id).get();
	}

	@Override
	public String updateProduct(Product product, String userName) {
		product.setUpdatedBy(userName);
		productRepository.save(product);
		return "Product updated successfully";
	}

}
