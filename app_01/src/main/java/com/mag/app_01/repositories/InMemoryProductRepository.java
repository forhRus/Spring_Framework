package com.mag.app_01.repositories;

import com.mag.app_01.entities.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Класс является имитацией работы с БД.
 * Реализует одноимённые методы, но хранит данные
 * во связном списке.
 */
@Repository
public class InMemoryProductRepository implements ProductRepository{

  // Database = Связный список (синхроннизированный).
  private final List<Product> products = Collections.synchronizedList(new LinkedList<>());

  /**
   * "Заполнение" БД товарами.
   */
  public InMemoryProductRepository() {
    int id = 0;
    products.add(new Product(++id, "Мяч", "Мяч для футбола"));
    products.add(new Product(++id, "Кукла", "Красивая мягкая кукла"));
    products.add(new Product(++id, "Машинка", "Машинка для самых маленьких"));
  }

  /**
   * Находит все товары, хранящиеся в БД.
   * @return список товаров.
   */
  @Override
  public List<Product> findAllProducts() {
    return Collections.unmodifiableList(this.products);
  }

  /**
   * Добавляет новый товар в список
   * @param product
   * @return
   */
  @Override
  public Product save(Product product) {
    // Установка id (максимальное значение id в списке + 1),
    // если список пустой, то (0 + 1)
    product.setId(this.products.stream()
            .max(Comparator.comparingInt(Product::getId))
            .map(Product::getId)
            .orElse(0)+1);

    this.products.add(product);
    return product;
  }

  /**
   * Находит товар по id.
   * @param productId id искомого товара.
   * @return
   */
  @Override
  public Optional<Product> findById(Integer productId) {
    // Сравнить полученный id с id товаров имеющихся в БД
    // и вернуть первое совпадение.
    return this.products.stream()
            .filter(p -> Objects.equals(productId, p.getId()))
            .findFirst();
  }

  /**
   * Удалить товар из БД по id
   * @param id - id товара
   */
  @Override
  public void deleteById(Integer id) {
    this.products.removeIf(p -> Objects.equals(id, p.getId()));
  }
}

