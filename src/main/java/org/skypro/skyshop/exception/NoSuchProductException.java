package org.skypro.skyshop.exception;

import java.util.UUID;

/**
 * Исключение, выбрасываемое когда товар не найден в хранилище
 */
public class NoSuchProductException extends RuntimeException {

    private final UUID productId;

    public NoSuchProductException(UUID productId) {
        super("Товар с ID " + productId + " не найден");
        this.productId = productId;
    }

    public UUID getProductId() {
        return productId;
    }
}
