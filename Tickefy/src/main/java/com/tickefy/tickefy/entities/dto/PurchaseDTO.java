package com.tickefy.tickefy.entities.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class PurchaseDTO {

    @NotBlank(message = "Card type is required.")
    private String cardType;

    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits.")
    private String cardNumber;

    @NotBlank(message = "Card holder name is required.")
    private String cardHolderName;

    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiration date must be in MM/YY format.")
    private String expirationDate;

    @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits.")
    private String cvvCode;

    @NotEmpty(message = "Cart items cannot be empty.")
    @Valid
    private List<CartItemDTO> cartItems;



    public PurchaseDTO() {}

    public PurchaseDTO(String cardType, String cardNumber, String cardHolderName, String expirationDate, String cvvCode, List<CartItemDTO> cartItems) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expirationDate = expirationDate;
        this.cvvCode = cvvCode;
        this.cartItems = cartItems;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    public List<CartItemDTO> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemDTO> cartItems) {
        this.cartItems = cartItems;
    }
}
