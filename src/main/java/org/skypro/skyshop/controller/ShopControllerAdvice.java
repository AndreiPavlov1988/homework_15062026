package org.skypro.skyshop.controller;

import org.skypro.skyshop.exception.NoSuchProductException;
import org.skypro.skyshop.model.error.ShopError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ShopControllerAdvice {

    @ExceptionHandler(NoSuchProductException.class)
    public ResponseEntity<ShopError> handleNoSuchProductException(NoSuchProductException ex) {
        // Создаем объект ошибки
        ShopError error = new ShopError(
                "PRODUCT_NOT_FOUND",           // код ошибки
                ex.getMessage()                // сообщение из исключения
        );

        // Возвращаем ответ с HTTP статусом 404 Not Found
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}
