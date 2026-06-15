package org.skypro.skyshop.model.product;
import java.util.UUID;

public class DiscountProduct extends Product {
    private final int discount;  // Скидка в процентах

    public DiscountProduct(UUID id, String name, int price, int discount) {
        super(id, name, price);
        this.discount = discount;
    }

    public int getDiscount() {
        return discount;
    }

    @Override
    public boolean isSpecial() {
        return true;  // Товар со скидкой считается специальным
    }
}