package com.mixo.utils;

import lombok.Data;

@Data
public class ProductDto {

	private String productId;
	private String productType;

	public ProductDto(String productId, String productType) {
		super();
		this.productId = productId;
		this.productType = productType;
	}

	public ProductDto() {
		super();
	}

}
