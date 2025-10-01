package com.mixo.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ProductScraper {
	public static void main(String[] args) {
		String url = "https://www.primeabgb.com/brand/wd/";

		try {
			// Load the page
			Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();

			// Select all product cards
			Elements products = doc.select("product-wrapper");

			for (Element product : products) {
				// Product name
				String name = product.select("product-title").text();

				// Price
				String price = product.select("woocommerce-Price-amount amount").first() != null
						? product.select("woocommerce-Price-amount amount").first().text()
						: "N/A";

				// Image URL
				String imageUrl = product.select("attachment-woocommerce_thumbnail size-woocommerce_thumbnail front-image").attr("src");

				// Output
				System.out.println("Name: " + name);
				System.out.println("Price: " + price);
				System.out.println("Image: " + imageUrl);
				System.out.println("-------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
