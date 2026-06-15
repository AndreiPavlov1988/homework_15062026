package org.skypro.skyshop.model.article;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.skypro.skyshop.model.search.Searchable;
import java.util.UUID;

public class Article implements Searchable {
    private final UUID id;
    private final String title;
    private final String content;

    public Article(UUID id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
    // ДОБАВЛЯЕМ ГЕТТЕР ДЛЯ id
    public UUID getId() {            // ← ДОБАВИТЬ
        return id;
    }
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    @JsonIgnore
    public String getSearchTerm() {
        // Ищем и по заголовку, и по содержимому
        return title + " " + content;
    }

    @Override
    @JsonIgnore
    public String getContentType() {
        return "ARTICLE";
    }
}