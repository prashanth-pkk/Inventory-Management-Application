package com.ims.service;

import com.ims.entity.Product;
import com.ims.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //create a new product
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    //find product by id
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product is not found"));
    }

    //get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    //update the product details
    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product is not found"));
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());

        return productRepository.save(product);
    }

    //Delete a product
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
