package com.indianecomliquidator.indianecomqc;

public class StoreDetailsInventory {

    //Data Variables
    private String productName;
    private String productImage;
    private String purchasePrice;
    private String salePrice;
    private String stockCount;
    private String stockUnit;
    private String productId;
    private String shopName;
    private String defaultPurchasePrice;
    private String defaultSalePrice;
    private String requestCount;
    private String mrp, productUnitSymbol, productUnitValue, inStock;

    //Getters and Setters

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(String stockUnit) {
        this.stockUnit = stockUnit;
    }

    public void setProductId(String product_id) {
        this.productId = product_id;
    }

    public String getProductId() {
        return productId;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getMrp() {
        return mrp;
    }

    public void setProductUnitSymbol(String productUnitSymbol) {
        this.productUnitSymbol = productUnitSymbol;
    }

    public String getProductUnitSymbol() {
        return productUnitSymbol;
    }

    public void setProductUnitValue(String productUnitValue) {
        this.productUnitValue = productUnitValue;
    }

    public String getProductUnitValue() {
        return productUnitValue;
    }

    public String getDefaultPurchasePrice() {
        return defaultPurchasePrice;
    }

    public void setDefaultPurchasePrice(String default_purchase_price) {
        this.defaultPurchasePrice = default_purchase_price;
    }

    public void setDefaultSalePrice(String default_sale_price) {
        this.defaultSalePrice = default_sale_price;
    }

    public String getDefaultSalePrice() {
        return defaultSalePrice;
    }

    public String getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(String requestCount) {
        this.requestCount = requestCount;
    }

    public void setInStock(String inStock) {
        this.inStock = inStock;
    }
    public String getInStock() {
        return inStock;
    }
}

