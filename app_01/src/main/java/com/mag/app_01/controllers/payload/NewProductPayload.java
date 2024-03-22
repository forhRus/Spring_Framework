package com.mag.app_01.controllers.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Используется для приёма данных
 * из формы создания нового товара
 * @param title название товара
 * @param description описание товара
 */
public record NewProductPayload(
        @NotNull(message = "{catalogue.products.create.errors.title_is_null}") // Не может быть пустым.
        @Size(min=3, max=50, message = "{catalogue.products.create.errors.title_size_is_invalid}") // Размер названия товара от 3 до 50 символов.
        String title,
        @Size(max=1000, message = "{catalogue.products.create.errors.description_size_is_invalid}") // Размер описания до 1000 символов.
        String description) {
}
