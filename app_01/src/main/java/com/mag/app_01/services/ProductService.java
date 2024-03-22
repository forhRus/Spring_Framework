package com.mag.app_01.services;

import com.mag.app_01.entities.Product;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс работы с entity Product
 */
public interface ProductService {

  /**
   * Найти все товары в БД
   * @return список товаров.
   */
  List<Product> findAllProducts();

  /**
   * Создать новый товар.
   * @param title - название товара.
   * @param description  - описание товара.
   * @return добавленный в БД товар.
   */
  Product createProduct(String title, String description);

  /**
   * Найти товар в БД по id
   * @param productId - id товара
   * @return optional с результатом поиска
   */
  Optional<Product> findProduct(Integer productId);

  /**
   * Изменить данные товара.
   * @param id - id товара
   * @param title - новое название товара.
   * @param description - новое описание товара.
   */
  void updateProduct(Integer id, String title, String description);

  /**
   * Удаляет товар из БД по id
   * @param id - id товара
   */
  void deleteProduct(Integer id);
}
