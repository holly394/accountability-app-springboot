package com.github.holly.accountability.purchase;

public class PurchaseDto {
    private Long id;
    private Double price = 0.00D;
    private String description;
    private String purchaseTimeString;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPurchaseTimeString() {
        return purchaseTimeString;
    }
    public void setPurchaseTimeString(String purchaseTimeString) {
        this.purchaseTimeString = purchaseTimeString;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
