package org.skypro.skyshop.model.search;
import java.util.UUID;

public interface Searchable {
    String getSearchTerm();  // Возвращает текст для поиска
    String getContentType();
    UUID getId(); // Возвращает тип контента (PRODUCT или ARTICLE)
}