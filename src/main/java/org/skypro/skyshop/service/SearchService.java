package org.skypro.skyshop.service;

import org.skypro.skyshop.model.search.SearchResult;
import org.skypro.skyshop.model.search.Searchable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final StorageService storageService;

    // Внедряем StorageService через конструктор
    public SearchService(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Поиск объектов по строковому шаблону
     * @param pattern строка для поиска (регистронезависимый поиск)
     * @return коллекция результатов поиска
     */
    public Collection<SearchResult> search(String pattern) {
        // Если шаблон пустой или null - возвращаем пустую коллекцию
        if (pattern == null || pattern.isBlank()) {
            return Collections.emptyList();
        }

        // Приводим шаблон к нижнему регистру для регистронезависимого поиска
        String lowerCasePattern = pattern.toLowerCase();

        // Stream API для фильтрации и преобразования
        return storageService.getAllSearchable().stream()
                // Фильтруем: оставляем только те, у кого searchTerm содержит pattern
                .filter(searchable -> searchable.getSearchTerm()
                        .toLowerCase()
                        .contains(lowerCasePattern))
                // Преобразуем Searchable в SearchResult
                .map(SearchResult::fromSearchable)
                // Собираем в список (можно и в любую другую коллекцию)
                .collect(Collectors.toList());
    }
}