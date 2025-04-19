package com.online.eShopping.service.impl;

import com.online.eShopping.exception.ResourceNotFoundException;
import com.online.eShopping.exception.InsufficientStockException;
import com.online.eShopping.mapper.ProductMapper;
import com.online.eShopping.model.Category;
import com.online.eShopping.repository.CategoryRepository;
import com.online.eShopping.dto.ProductCreateDTO;
import com.online.eShopping.dto.ProductDTO;
import com.online.eShopping.dto.ProductUpdateDTO;
import com.online.eShopping.model.Category;
import com.online.eShopping.model.Product;
import com.online.eShopping.repository.ProductRepository;
import com.online.eShopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(int page, int size) {
        log.info("Fetching all products page {} with size {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size) {
        log.info("Fetching products by category id: {}", categoryId);

        // Kontrolli, kas kategooria eksisteerib
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        return productPage.map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, int page, int size) {
        log.info("Searching products with keyword: {}", keyword);
        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository.search(keyword, pageable);
        return productPage.map(productMapper::toDTO);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {
        log.info("Creating new product: {}", productCreateDTO.getName());

        // Leia kategooria
        Category category = categoryRepository.findById(productCreateDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productCreateDTO.getCategoryId()));

        // Kaardista DTO Product üksuseks
        Product product = productMapper.toEntity(productCreateDTO);
        product.setCategory(category);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        // Salvesta toode
        Product savedProduct = productRepository.save(product);

        log.info("Product created with id: {}", savedProduct.getId());
        return productMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) {
        log.info("Updating product with id: {}", id);

        // Kontrolli, kas toode eksisteerib
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Uuenda olemasolevat toodet
        if (productUpdateDTO.getName() != null) {
            existingProduct.setName(productUpdateDTO.getName());
        }

        if (productUpdateDTO.getDescription() != null) {
            existingProduct.setDescription(productUpdateDTO.getDescription());
        }

        if (productUpdateDTO.getPrice() != null) {
            existingProduct.setPrice(productUpdateDTO.getPrice());
        }

        if (productUpdateDTO.getStockQuantity() != null) {
            existingProduct.setStockQuantity(productUpdateDTO.getStockQuantity());
        }

        if (productUpdateDTO.getImageUrl() != null) {
            existingProduct.setImageUrl(productUpdateDTO.getImageUrl());
        }

        if (productUpdateDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productUpdateDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productUpdateDTO.getCategoryId()));
            existingProduct.setCategory(category);
        }

        existingProduct.setUpdatedAt(LocalDateTime.now());

        // Salvesta uuendatud toode
        Product updatedProduct = productRepository.save(existingProduct);

        log.info("Product updated with id: {}", updatedProduct.getId());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);

        // Kontrolli, kas toode eksisteerib
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted with id: {}", id);
    }

    @Override
    @Transactional
    public void reduceStock(Long productId, int quantity) {
        log.info("Reducing stock for product id: {} by quantity: {}", productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
        log.info("Stock reduced for product id: {}", productId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInStock(Long productId, int quantity) {
        log.info("Checking stock for product id: {} with quantity: {}", productId, quantity);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return product.getStockQuantity() >= quantity;
    }
}
