package org.skypro.skyshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.exception.NoSuchProductException;
import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private ProductBasket productBasket;  // Мок для корзины

    @Mock
    private StorageService storageService;  // Мок для хранилища

    @InjectMocks
    private BasketService basketService;  // Тестируемый сервис

    private UUID productId1;
    private UUID productId2;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();

        testProduct1 = new SimpleProduct(productId1, "Ноутбук", 50000);
        testProduct2 = new SimpleProduct(productId2, "Мышь", 1500);
    }

    // СЦЕНАРИЙ 1: Добавление несуществующего товара -> исключение
    @Test
    void addProductToBasket_shouldThrowException_whenProductNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(storageService.getProductById(nonExistentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchProductException.class, () -> {
            basketService.addProductToBasket(nonExistentId);
        });

        // Проверяем, что addProduct у корзины НЕ вызывался
        verify(productBasket, never()).addProduct(any());
        verify(storageService, times(1)).getProductById(nonExistentId);
    }

    // СЦЕНАРИЙ 2: Добавление существующего товара -> вызов addProduct
    @Test
    void addProductToBasket_shouldCallAddProduct_whenProductExists() {
        // Arrange
        when(storageService.getProductById(productId1))
                .thenReturn(Optional.of(testProduct1));

        // Act
        basketService.addProductToBasket(productId1);

        // Assert
        verify(productBasket, times(1)).addProduct(productId1);
        verify(storageService, times(1)).getProductById(productId1);
    }

    // СЦЕНАРИЙ 3: getUserBasket возвращает пустую корзину
    @Test
    void getUserBasket_shouldReturnEmptyBasket_whenProductBasketIsEmpty() {
        // Arrange
        when(productBasket.getProducts()).thenReturn(Collections.emptyMap());

        // Act
        UserBasket userBasket = basketService.getUserBasket();

        // Assert
        assertNotNull(userBasket);
        assertTrue(userBasket.getItems().isEmpty());
        assertEquals(0, userBasket.getTotal());

        verify(productBasket, times(1)).getProducts();
        verify(storageService, never()).getProductById(any());
    }

    // СЦЕНАРИЙ 4: getUserBasket возвращает корзину с товарами
    @Test
    void getUserBasket_shouldReturnBasketWithItems_whenProductBasketHasItems() {
        // Arrange
        Map<UUID, Integer> basketMap = new HashMap<>();
        basketMap.put(productId1, 2);  // 2 ноутбука
        basketMap.put(productId2, 3);  // 3 мыши

        when(productBasket.getProducts()).thenReturn(basketMap);
        when(storageService.getProductById(productId1))
                .thenReturn(Optional.of(testProduct1));
        when(storageService.getProductById(productId2))
                .thenReturn(Optional.of(testProduct2));

        // Act
        UserBasket userBasket = basketService.getUserBasket();

        // Assert
        assertNotNull(userBasket);
        assertEquals(2, userBasket.getItems().size());

        // Проверяем общую стоимость: 2*50000 + 3*1500 = 100000 + 4500 = 104500
        assertEquals(104500, userBasket.getTotal());

        // Проверяем содержимое корзины
        for (BasketItem item : userBasket.getItems()) {
            if (item.getProduct().getId().equals(productId1)) {
                assertEquals(2, item.getQuantity());
                assertEquals("Ноутбук", item.getProduct().getName());
            } else if (item.getProduct().getId().equals(productId2)) {
                assertEquals(3, item.getQuantity());
                assertEquals("Мышь", item.getProduct().getName());
            }
        }

        verify(productBasket, times(1)).getProducts();
        verify(storageService, times(1)).getProductById(productId1);
        verify(storageService, times(1)).getProductById(productId2);
    }

    // СЦЕНАРИЙ 5: getUserBasket с одним товаром
    @Test
    void getUserBasket_shouldReturnBasketWithSingleItem() {
        // Arrange
        Map<UUID, Integer> basketMap = new HashMap<>();
        basketMap.put(productId1, 5);  // 5 ноутбуков

        when(productBasket.getProducts()).thenReturn(basketMap);
        when(storageService.getProductById(productId1))
                .thenReturn(Optional.of(testProduct1));

        // Act
        UserBasket userBasket = basketService.getUserBasket();

        // Assert
        assertNotNull(userBasket);
        assertEquals(1, userBasket.getItems().size());
        assertEquals(250000, userBasket.getTotal()); // 5 * 50000

        BasketItem item = userBasket.getItems().get(0);
        assertEquals(productId1, item.getProduct().getId());
        assertEquals(5, item.getQuantity());
    }

    // СЦЕНАРИЙ 6: getUserBasket с нулевым количеством товара
    @Test
    void getUserBasket_shouldHandleZeroQuantity() {
        // Arrange
        Map<UUID, Integer> basketMap = new HashMap<>();
        basketMap.put(productId1, 0);  // 0 ноутбуков

        when(productBasket.getProducts()).thenReturn(basketMap);
        when(storageService.getProductById(productId1))
                .thenReturn(Optional.of(testProduct1));

        // Act
        UserBasket userBasket = basketService.getUserBasket();

        // Assert
        assertNotNull(userBasket);
        assertEquals(1, userBasket.getItems().size());
        assertEquals(0, userBasket.getTotal()); // 0 * 50000

        BasketItem item = userBasket.getItems().get(0);
        assertEquals(0, item.getQuantity());
    }

    // ДОПОЛНИТЕЛЬНЫЙ СЦЕНАРИЙ: Добавление нескольких разных товаров
    @Test
    void addProductToBasket_shouldWorkWithMultipleProducts() {
        // Arrange
        when(storageService.getProductById(productId1))
                .thenReturn(Optional.of(testProduct1));
        when(storageService.getProductById(productId2))
                .thenReturn(Optional.of(testProduct2));

        // Act
        basketService.addProductToBasket(productId1);
        basketService.addProductToBasket(productId2);
        basketService.addProductToBasket(productId1); // Добавляем ноутбук еще раз

        // Assert
        verify(productBasket, times(2)).addProduct(productId1);
        verify(productBasket, times(1)).addProduct(productId2);
        verify(storageService, times(3)).getProductById(any());
    }

    // СЦЕНАРИЙ: Проверка что корзина защищена от изменений
    @Test
    void getUserBasket_shouldReturnUnmodifiableList() {
        // Arrange
        Map<UUID, Integer> basketMap = new HashMap<>();
        basketMap.put(productId1, 1);

        when(productBasket.getProducts()).thenReturn(basketMap);
        when(storageService.getProductById(productId1))
                .thenReturn(Optional.of(testProduct1));

        // Act
        UserBasket userBasket = basketService.getUserBasket();

        // Assert - пытаемся изменить список
        List<BasketItem> items = userBasket.getItems();
        assertThrows(UnsupportedOperationException.class, () -> {
            items.add(new BasketItem(testProduct2, 1));
        });
    }
}
