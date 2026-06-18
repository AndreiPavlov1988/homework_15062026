package org.skypro.skyshop.service;

import org.skypro.skyshop.exception.NoSuchProductException;
import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.product.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasketService {

    private final ProductBasket productBasket;
    private final StorageService storageService;

    public BasketService(ProductBasket productBasket, StorageService storageService) {
        this.productBasket = productBasket;
        this.storageService = storageService;
    }

    /**
     * Добавление товара в корзину
     * @param id идентификатор товара
     * @throws IllegalArgumentException если товар не найден
     */
    public void addProductToBasket(UUID id) {
        // Проверяем, существует ли товар
        Product product = storageService.getProductById(id)
                .orElseThrow(() -> new NoSuchProductException(id));

        // Добавляем в корзину
        productBasket.addProduct(product.getId());
    }

    /**
     * Получение корзины пользователя для отображения
     * @return UserBasket с продуктами и общей стоимостью
     */
    public UserBasket getUserBasket() {
        // Получаем Map из корзины
        var basketMap = productBasket.getProducts();

        // Преобразуем в список BasketItem
        List<BasketItem> items = basketMap.entrySet().stream()
                .map(entry -> {
                    UUID productId = entry.getKey();
                    Integer quantity = entry.getValue();

                    // Получаем продукт из хранилища (должен существовать)
                    Product product = storageService.getProductById(productId)
                            .orElseThrow(() -> new IllegalStateException("Продукт " + productId + " не найден в хранилище"));

                    return new BasketItem(product, quantity);
                })
                .collect(Collectors.toList());

        return new UserBasket(items);
    }
}
