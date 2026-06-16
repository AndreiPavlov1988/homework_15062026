package org.skypro.skyshop.model.basket;

import org.skypro.skyshop.model.product.Product;

/**
 * Элемент корзины - продукт и его количество
 */
public class BasketItem {
    private final Product product;
    private final int quantity;

    public BasketItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
