package com.mag.app_01.repositories;

import com.mag.app_01.entities.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс работы с БД
 */
public interface ProductRepository {

  /**
   * Найти все товары в БД
   * @return список товаров
   */
  List<Product> findAllProducts();

  /**
   * Добавить новый товар в БД
   * @param product - новый товар
   * @return
   */
  Product save(Product product);

  /**
   * Найти товар по id
   * @param productId - id товара
   * @return результат поиска
   */
  Optional<Product> findById(Integer productId);

  /**
   * Удаляет товар из БД по id
   * @param id - id товара
   */
  void deleteById(Integer id);
}
