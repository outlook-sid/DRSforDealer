package com.example.drsfordealer.database;

public class RationItemModel {
    private String MaxPerHead;
    private String Price;
    private String itemName;
    private String subtotal;

    public RationItemModel( ) {
    }

    public RationItemModel(String maxPerHead, String price) {
        MaxPerHead = maxPerHead;
        Price = price;
    }

    public RationItemModel(String maxPerHead, String price, String itemName) {
        MaxPerHead = maxPerHead;
        Price = price;
        this.itemName = itemName;
        this.subtotal = subtotal;
    }

    public String getMaxPerHead() {
        return MaxPerHead;
    }

    public void setMaxPerHead(String maxPerHead) {
        MaxPerHead = maxPerHead;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSubtotal() {
        subtotal = String.valueOf(Float.parseFloat(Price)*Float.parseFloat(MaxPerHead));
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
}

