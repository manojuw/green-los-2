package com.mixo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mixo.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByUid(String uid);

	Optional<Product> findByProductId(String productId);

	Optional<Product> findByProductIdAndUid(String productId, String uid);
	

	Optional<Product> findById(Long id);

}
