package com.mag.app_01.controllers;

import com.mag.app_01.controllers.payload.NewProductPayload;
import com.mag.app_01.entities.Product;
import com.mag.app_01.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/catalogue/products") // Общий префикс для всех запросов контроллера
@RequiredArgsConstructor // Создаёт конструктор из final или не null полей
public class ProductsController {

  // сервис со всей логикой
  private final ProductService productService; // Зависим от интерфейсов

  /**
   * Запрос на показ списка товаров
   */
//  @RequestMapping(value = "list", method = RequestMethod.GET) // Устаревший вариант
  @GetMapping("/list")
  public String getProductsList(Model model){
    model.addAttribute("products", this.productService.findAllProducts());
    return "catalogue/products/list";
  }

  /**
   * Отдаёт шаблон с формой для создания нового товара.
   * @return Имя шаблона с формой для создания нового товара.
   */
  @GetMapping("/new")
  public String newProductPage(){
    return "catalogue/products/new";
  }

  /**
   * Принимает содержимое формы,
   * И отправляет их для создания нового товара.
   * @param payload Содержимое формы для создания нового товара
   * @return имя шаблона с данными товара
   */
  @PostMapping("/new")
  public String createProduct(@Valid NewProductPayload payload, // валидируем параметр
                              BindingResult bindingResult,
                              Model model){

    if (bindingResult.hasErrors()){

      // Чтобы пользователь не потерял введённые значения.
      model.addAttribute("payload", payload);

      // Преобразование ошибок в список строк.
      model.addAttribute("errors", bindingResult.getAllErrors().stream()
              .map(ObjectError::getDefaultMessage)
              .toList());

      // Возврат на форму создания нового товара.
      return "catalogue/products/new";
    } else{
      Product product = this.productService.createProduct(payload.title(), payload.description());
      model.addAttribute("product", product);
      return "redirect:/catalogue/product/%d/info".formatted(product.getId());
//    return "catalogue/product/info";
    }
  }

/*
  Перенесены в ProductController из-за @ModelAttribute

  @GetMapping()
  public String getProduct(@PathVariable("productId") int productId,
                           Model model){
    Product product = this.productService.findProduct(productId).orElseThrow();
    model.addAttribute("product", product);
    return "catalogue/product/info";
  }

  @GetMapping("/edit")
  public String getProductEditPage(@PathVariable("productId") int productId,
                                   Model model){
    Product product = this.productService.findProduct(productId).orElseThrow();
    model.addAttribute("product", product);
    return "catalogue/product/edit";
  }
 */

}
