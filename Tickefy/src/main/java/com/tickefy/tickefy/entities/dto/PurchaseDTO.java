package com.tickefy.tickefy.entities.dto;


import java.util.List;

public class PurchaseDTO {

    private String cardType;

    private String cardNumber;

    private String cardHolderName;

    private String expirationDate;

    private String cvvCode;

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
