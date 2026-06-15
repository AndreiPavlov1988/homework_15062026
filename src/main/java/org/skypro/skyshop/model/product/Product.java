package org.skypro.skyshop.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.skypro.skyshop.model.search.Searchable;
import java.util.UUID;

public abstract class Product implements Searchable {
    private final UUID id;
    private final String name;
    private final int price;

    public Product(UUID id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
    public UUID getId() {                               // ← ДОБАВИТЬ
        return id;
    }
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    @JsonIgnore
    public String getSearchTerm() {
        return getName();
    }

    @Override
    @JsonIgnore
    public String getContentType() {
        return "PRODUCT";
    }

    public abstract boolean isSpecial();
}