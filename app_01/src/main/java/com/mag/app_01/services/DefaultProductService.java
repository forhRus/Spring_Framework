package com.mag.app_01.services;

import com.mag.app_01.entities.Product;
import com.mag.app_01.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Реализует методы ProductService.
 * Содержит всю логику работы с entity Product
 */
@Service
@RequiredArgsConstructor
public class DefaultProductService implements ProductService{

  // работает с БД
  private final ProductRepository productRepository;

  /**
   *
   * @return
   */
  @Override
  public List<Product> findAllProducts() {
    return this.productRepository.findAllProducts();
  }

  /**
   * Создать новый товар.
   * @param title - название товара.
   * @param description  - описание товара.
   * @return
   */
  @Override
  public Product createProduct(String title, String description) {
    return this.productRepository.save(new Product(null, title, description));

  }

  /**
   * Найти товар в БД по id
   * @param productId - id товара
   * @return
   */
  @Override
  public Optional<Product> findProduct(Integer productId) {
    return this.productRepository.findById(productId);
  }

  /**
   * Изменить данные товара.
   * @param id - id товара
   * @param title - новое название товара.
   * @param description - новое описание товара.
   */
  @Override
  public void updateProduct(Integer id, String title, String description) {
    this.productRepository.findById(id)
            .ifPresentOrElse(p -> {
              p.setTitle(title);
              p.setDescription(description);
            }, () ->{
              throw new NoSuchElementException();
            });
  }

  /**
   * Удаляет товар из БД
   * @param id - id товара
   */
  @Override
  public void deleteProduct(Integer id) {
    this.productRepository.deleteById(id);
  }
}
