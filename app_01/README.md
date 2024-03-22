# Простое MVC приложение со списком товаров.
Класс Product (Товар) хранит в себе:
* id - идентификатор, 
* title - наименование товара,
* desctiption - описание товара.

Приложение позволяет (CRUD):
1. Получать список всех товаров - /catalogue/products/list
2. Добавлять новый товар (работаем пока со списком) - /catalogue/products/new,
2. Получать карточку товара с данными /catalogue/products/{id}/info, 
3. Изменять данные о товаре - /catalogue/products/{id}/edit,
4. Удалять товар - /catalogue/products/{id}/delete.

Обработку запросов осуществляют два контроллера:
* ProductController
  * чтение данных товара по id,
  * изменение данных товара по id,
  * удаление товара по id.
* ProductsContoller 
  * чтение всех данных,
  * добавление нового товара в список.

## ProductsController

```agsl
@Controller
@RequiredArgsConstructor
@RequestMapping("/catalogue/product/{productId:\\d+}")
public class ProductController {...}
```

Запрос (/catalogue/products) обрабатывает ProductsContoller, который осуществляет
манипуляции со списком товаров.

Для того чтобы ко всем запросам контроллера добавлялся определённый
URL используем `@RequestMapping("<url>")`

```agsl
private final ProductService productService;
```

Контроллёр работает с сервисом, который в свою очередь работает с БД (в моём случае список).

### Получение списка товаров.

```agsl
@GetMapping("/list")
public String getProductsList(Model model){
    model.addAttribute("products", this.productService.findAllProducts());
    return "catalogue/products/list";
}
```

Аннотация `@GetMapping("/list")`, указывает на то, что метод обрабатывает запрос 
`/catalogue/products/list`. По этому запросу мы возвращаем список со всеми товарами.

Чтобы добавить в шаблон какие-либо данные необходимо в параметрах метода принять
Model и добавить в неё данные.

Методы контроллёра обычно возвращают строку (имя шаблона, html страницы), 
если это не REST приложение.

### Создание нового товара.

```agsl
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
```

Метод помечен как `@PostMapping("/new")` и поэтмоу он обрабатывает post-запрос `/catalogue/products/new`.
В качестве параметра принимает данные из формы (данные проходят проверку), автоматически создавая объект 
`NewProductPayload`

Валидацию опускаю.

В результате работы метода, программа либо возвращает шаблон с формой для создания товара (+ошибки),
либо `return "redirect:/catalogue/product/%d/info".formatted(product.getId());`, перенаправляет пользователя
на другой запрос, вместо того, чтобы вернуть ему представление.

## ProductController

Обрабатывает запросы связанные с конкретным товаром.
`"/catalogue/product/{productId:\\d+}"` - принимает в параметрах id (обязательно число).

```agsl
@ModelAttribute("product") // имя атрибута в модели
public Product product(@PathVariable("productId") int productId){ // получаем переменную пути

    // Выбрасываем NoSuchElementException со сообщением из файла message.properties
    return this.productService.findProduct(productId)
            .orElseThrow(() -> new NoSuchElementException("errors.product.not_found"));
}
```

С помощью аннотации `@ModelAttribute("<key>")`, помещённой над методом, мы добавляем в Model данные, 
которые возвращает отмеченные метод.
В качестве данных служит полученный из списка товаров (БД) товар с переданным id или выбрасываем исключение, 
что товар с таким id не найден.

### Чтение данных о товаре.

По запросу `/catalogue/product/{productId:\d+}/info` метод возвращает шаблон, в модель которого
уже добавлен product с переданным в параметрах id

### Редактирование данных о товаре.

По get-запросу `/catalogue/product/{productId:\d+}/edit` метод возвращает шаблон, с формой для изменения данных
товара. В модель уже добавлен product с переданным в параметрах id, поэтому мы можем отобразить значения полей в форме.

По post-запросу `/catalogue/product/{productId:\d+}/edit` метод принимает данные формы и с помощью `@ModelAttribute("<key>")`
в параметрах методы мы одновременно создаём из данных в теле запроса объект товара и добавляем этот объект в Model шаблона.

`@ModelAttribute(value = "product", binding = false)`
  * value - на самом деле ключ, по которому мы будем получать доступ к данным в шаблоне.
  * binding - позволяет нам не связывать данные товара и изменённые данные. Потому что иначе 
первоначальные данные о товаре для отображения будут уже не доступны. Их заменят изменённые данные (только в шаблоне, не в БД)

### Удаление товара.

По запросу `/catalogue/product/{productId:\d+}/delete` мы удаляем твоар из БД. Все изменения производятся через post-запрос.
Перенаправляем на список всех твоаров (удалённого уже не будет).

### Перехват исключений

Аннотация `@ExceptionHandler(NoSuchElementException.class)` позволяет перехватить нам определённый тип 
исключений, чтобы реализовать кастомную страницу для ошибки 404.
