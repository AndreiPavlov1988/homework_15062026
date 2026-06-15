package org.skypro.skyshop.model.product;
import java.util.UUID;

public class FixPriceProduct extends Product {
    private static final int FIX_PRICE = 100;  // Фиксированная цена для всех товаров

    public FixPriceProduct(UUID id, String name) {
        super(id, name, FIX_PRICE);  // Всегда используем фиксированную цену
    }

    @Override
    public boolean isSpecial() {
        return true;  // Товар с фиксированной ценой специальный
    }
}