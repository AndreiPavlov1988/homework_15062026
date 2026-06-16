package org.skypro.skyshop.service;

import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.DiscountProduct;
import org.skypro.skyshop.model.product.FixPriceProduct;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.Searchable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Optional;

@Service
public class StorageService {

    private final Map<UUID, Product> products;
    private final Map<UUID, Article> articles;

    public StorageService() {
        this.products = new HashMap<>();
        this.articles = new HashMap<>();
        initTestData();
    }

    private void initTestData() {
        // ... существующий код с тестовыми данными ...
        Product product1 = new SimpleProduct(UUID.randomUUID(), "Ноутбук", 50000);
        Product product2 = new DiscountProduct(UUID.randomUUID(), "Мышь", 1500, 10);
        Product product3 = new FixPriceProduct(UUID.randomUUID(), "Коврик для мыши");

        products.put(product1.getId(), product1);
        products.put(product2.getId(), product2);
        products.put(product3.getId(), product3);

        Article article1 = new Article(UUID.randomUUID(),
                "Как выбрать ноутбук",
                "Советы по выбору идеального ноутбука для работы и игр");
        Article article2 = new Article(UUID.randomUUID(),
                "Обзор игровых мышей",
                "Топ-5 лучших игровых мышей 2024 года");

        articles.put(article1.getId(), article1);
        articles.put(article2.getId(), article2);
    }

    public Collection<Product> getAllProducts() {
        return products.values();
    }

    public Collection<Article> getAllArticles() {
        return articles.values();
    }

    // НОВЫЙ МЕТОД - объединяет все объекты для поиска
    public Collection<Searchable> getAllSearchable() {
        Collection<Searchable> result = new ArrayList<>();
        // Добавляем все продукты (Product implements Searchable)
        result.addAll(products.values());
        // Добавляем все статьи (Article implements Searchable)
        result.addAll(articles.values());
        return result;
    }
    public Optional<Product> getProductById(UUID id) {
        return Optional.ofNullable(products.get(id));
    }
}