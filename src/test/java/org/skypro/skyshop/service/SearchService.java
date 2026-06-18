package org.skypro.skyshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.SearchResult;
import org.skypro.skyshop.model.search.Searchable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private StorageService storageService;  // Мок для StorageService

    @InjectMocks
    private SearchService searchService;    // Тестируемый сервис с внедренным моком

    private UUID productId1;
    private UUID productId2;
    private UUID articleId1;
    private Product testProduct;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        productId1 = UUID.randomUUID();
        productId2 = UUID.randomUUID();
        articleId1 = UUID.randomUUID();

        testProduct = new SimpleProduct(productId1, "Ноутбук", 50000);
        testArticle = new Article(articleId1, "Как выбрать ноутбук", "Советы по выбору ноутбука");
    }

    // СЦЕНАРИЙ 1: Поиск при пустом StorageService
    @Test
    void search_shouldReturnEmptyList_whenStorageIsEmpty() {
        // Arrange
        when(storageService.getAllSearchable()).thenReturn(Collections.emptyList());

        // Act
        Collection<SearchResult> results = searchService.search("ноут");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(storageService, times(1)).getAllSearchable();
    }

    // СЦЕНАРИЙ 2: Поиск, когда объекты есть, но нет совпадений
    @Test
    void search_shouldReturnEmptyList_whenNoMatchesFound() {
        // Arrange
        Collection<Searchable> items = List.of(testProduct, testArticle);
        when(storageService.getAllSearchable()).thenReturn(items);

        // Act
        Collection<SearchResult> results = searchService.search("телефон");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(storageService, times(1)).getAllSearchable();
    }

    // СЦЕНАРИЙ 3: Поиск, когда есть совпадения
    @Test
    void search_shouldReturnResults_whenMatchesFound() {
        // Arrange
        Collection<Searchable> items = List.of(testProduct, testArticle);
        when(storageService.getAllSearchable()).thenReturn(items);

        // Act
        Collection<SearchResult> results = searchService.search("ноут");

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size()); // Должны найтись и продукт, и статья

        // Проверяем, что все результаты содержат "ноут"
        results.forEach(result ->
                assertTrue(result.getName().toLowerCase().contains("ноут"))
        );

        verify(storageService, times(1)).getAllSearchable();
    }

    // СЦЕНАРИЙ 4: Поиск с пустым шаблоном
    @Test
    void search_shouldReturnEmptyList_whenPatternIsEmpty() {
        // УБИРАЕМ НЕНУЖНЫЙ СТАБ - он не используется, т.к. метод возвращается раньше
        // when(storageService.getAllSearchable()).thenReturn(items);  // ← УДАЛИТЬ ЭТУ СТРОКУ

        // Act
        Collection<SearchResult> results = searchService.search("");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        // Проверяем, что метод getAllSearchable НЕ вызывался
        verify(storageService, never()).getAllSearchable();
    }

    // СЦЕНАРИЙ 5: Поиск с null шаблоном
    @Test
    void search_shouldReturnEmptyList_whenPatternIsNull() {
        // Act
        Collection<SearchResult> results = searchService.search(null);

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(storageService, never()).getAllSearchable();
    }

    // СЦЕНАРИЙ 6: Поиск с учетом регистра
    @Test
    void search_shouldBeCaseInsensitive() {
        // Arrange
        // Создаем продукт с названием "Ноутбук" и статью без слова "ноут"
        Product product = new SimpleProduct(productId1, "Ноутбук", 50000);
        Article article = new Article(articleId1, "Как выбрать компьютер", "Советы по выбору ПК");

        Collection<Searchable> items = List.of(product, article);
        when(storageService.getAllSearchable()).thenReturn(items);

        // Act - ищем в ВЕРХНЕМ регистре
        Collection<SearchResult> resultsUpperCase = searchService.search("НОУТ");

        // Act - ищем в смешанном регистре
        Collection<SearchResult> resultsMixedCase = searchService.search("НоутБук");

        // Assert
        assertEquals(1, resultsUpperCase.size());  // Должен найтись только продукт "Ноутбук"
        assertEquals(1, resultsMixedCase.size());  // Должен найтись только продукт "Ноутбук"

        // Проверяем, что найден именно продукт
        SearchResult result = resultsUpperCase.iterator().next();
        assertEquals("PRODUCT", result.getContentType());
        assertTrue(result.getName().toLowerCase().contains("ноут"));

        verify(storageService, times(2)).getAllSearchable();
    }

    // ДОПОЛНИТЕЛЬНЫЙ СЦЕНАРИЙ: Поиск только по продуктам
    @Test
    void search_shouldFindOnlyProducts_whenSearchTermMatchesProduct() {
        // Arrange
        Product product2 = new SimpleProduct(productId2, "Мышь", 1500);
        Collection<Searchable> items = List.of(testProduct, testArticle, product2);
        when(storageService.getAllSearchable()).thenReturn(items);

        // Act
        Collection<SearchResult> results = searchService.search("мышь");

        // Assert
        assertEquals(1, results.size());
        SearchResult result = results.iterator().next();
        assertEquals("PRODUCT", result.getContentType());
        assertTrue(result.getName().toLowerCase().contains("мышь"));
    }

    // ДОПОЛНИТЕЛЬНЫЙ СЦЕНАРИЙ: Поиск только по статьям
    @Test
    void search_shouldFindOnlyArticles_whenSearchTermMatchesArticle() {
        // Arrange
        Collection<Searchable> items = List.of(testProduct, testArticle);
        when(storageService.getAllSearchable()).thenReturn(items);

        // Act
        Collection<SearchResult> results = searchService.search("выбрать");

        // Assert
        assertEquals(1, results.size());
        SearchResult result = results.iterator().next();
        assertEquals("ARTICLE", result.getContentType());
        assertTrue(result.getName().toLowerCase().contains("выбрать"));
    }

    // СЦЕНАРИЙ: Проверка преобразования Searchable в SearchResult
    @Test
    void search_shouldConvertSearchableToSearchResult() {
        // Arrange
        Collection<Searchable> items = List.of(testProduct);
        when(storageService.getAllSearchable()).thenReturn(items);

        // Act
        Collection<SearchResult> results = searchService.search("ноут");

        // Assert
        assertEquals(1, results.size());
        SearchResult result = results.iterator().next();

        assertEquals(productId1.toString(), result.getId());
        assertEquals("Ноутбук", result.getName());
        assertEquals("PRODUCT", result.getContentType());
    }
}
