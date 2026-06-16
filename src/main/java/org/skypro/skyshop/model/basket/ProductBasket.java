package org.skypro.skyshop.model.basket;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component  // Регистрируем как Spring-бин
@SessionScope  // ВАЖНО! Будет создан новый экземпляр для каждой сессии
public class ProductBasket {

    // Хранилище: ключ - UUID товара, значение - количество
    private final Map<UUID, Integer> products = new HashMap<>();

    /**
     * Добавление продукта в корзину
     * @param id идентификатор продукта
     */
    public void addProduct(UUID id) {
        // Увеличиваем количество на 1 (или устанавливаем 1, если товара еще нет)
        products.merge(id, 1, Integer::sum);
    }

    /**
     * Получение всех продуктов в корзине
     * @return неизменяемая Map с продуктами и их количеством
     */
    public Map<UUID, Integer> getProducts() {
        // Защита от изменений - возвращаем неизменяемую обертку
        return Collections.unmodifiableMap(products);
    }
}
