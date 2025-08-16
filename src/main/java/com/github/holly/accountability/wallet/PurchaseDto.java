package com.github.holly.accountability.wallet;

public class PurchaseDto {
    private Long id;
    private Float price = 0.00F;
    private String description;
    private String purchaseTimeString;

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
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
