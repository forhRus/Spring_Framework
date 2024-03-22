package com.mag.app_01.controllers;

import com.mag.app_01.controllers.payload.UpdateProductPayload;
import com.mag.app_01.entities.Product;
import com.mag.app_01.services.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Контроллер обрабатывает пути с id товаров.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/catalogue/product/{productId:\\d+}")
public class ProductController {

  private final ProductService productService;
  // Сообщения из файла messages.properties
  private final MessageSource messageSource;

  /**
   * Добавляет атрибут товара в модель всех запросов.
   * @param productId принимает в переменной пу ти id товара
   * @return товар и
   */
  @ModelAttribute("product") // имя атрибута в модели
  public Product product(@PathVariable("productId") int productId){ // получаем переменную пути

    /*
    // Выбрасываем исключение с сообщением по умолчанию
    return this.productService.findProduct(productId).orElseThrow();
     */

    /*
    // выбрасываем NoSuchElementException со своим сообщением
    return this.productService.findProduct(productId)
            .orElseThrow(() -> new NoSuchElementException("Товар не найден."));
     */

    // Выбрасываем NoSuchElementException со сообщением из файла message.properties
    return this.productService.findProduct(productId)
            .orElseThrow(() -> new NoSuchElementException("errors.product.not_found"));
  }

  /**
   * Шаблон с данными о товаре (с переданным id).
   * @return
   */
  @GetMapping("/info")
  public String getProduct(){
    return "catalogue/product/info";
  }

  /**
   * Отдаёт шаблон с формой для изменения данных
   * о товаре (с переданным id)
   * @return
   */
  @GetMapping("/edit")
  public String getProductEditPage(){
    return "catalogue/product/edit";
  }

  /**
   * Редактирование данных товара
   * @param product
   * @param payload
   * @return
   */
  @PostMapping("/edit")
  public String getProductEditPage(@ModelAttribute(value = "product", binding = false) Product product,
                                   @Validated UpdateProductPayload payload, // аналог @Valid
                                   Errors errors,
                                   Model model) { // аналог BindingResult

    if(errors.hasErrors()){
      // Чтобы пользователь не потерял введённые значения.
      model.addAttribute("payload", payload);

      // Преобразование ошибок в список строк.
      model.addAttribute("errors", errors.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .toList());

      // Возврат на форму редактирования товара.
      return "catalogue/product/edit";
    } else{
      this.productService.updateProduct(product.getId(), payload.title(), payload.description());
      return "redirect:/catalogue/product/%d/info".formatted(product.getId());
    }

  }

  /**
   * Удаление товара
   * @param product
   * @return
   */
  @PostMapping("/delete")
  public String deleteProduct(@ModelAttribute("product") Product product){
    this.productService.deleteProduct(product.getId());
    return "redirect:/catalogue/products/list";
  }

  /**
   * Отлавливаем исключение NoSuchElementException.class
   */
  @ExceptionHandler(NoSuchElementException.class)
  public String handleNoSuchElementException(NoSuchElementException exception, // доступ к исключению
                                             Model model,
                                             HttpServletResponse response,
                                             Locale locale){
    response.setStatus(HttpStatus.NOT_FOUND.value()); // возвращаем статус 404
    model.addAttribute("error",
            // Используем значение из файла по ключу(получаем из исключения),
            // передаём массив значений для форматирования сообщения (у нас обычная строка, поэтому пустой массив),
            // Сообщение по умолчанию, если не будет найден ключ в файле
            // локаль для настройки языка вывода
            this.messageSource.getMessage(exception.getMessage(), new Object[0],
                    exception.getMessage(), locale));
    return "errors/404";

  }
}
