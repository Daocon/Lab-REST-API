package com.example.lab1.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Fruit {
    private String _id, name, quantity, price, status;
    private ArrayList<String> image;
    private String description;
    @SerializedName("id_distributor")
    private Distributor distributor;
    private String createAt, updateAt;

    public Fruit() {
    }

    public Fruit(String _id, String name, String quantity, String price, String status, ArrayList<String> image, String description, Distributor distributor, String createAt, String updateAt) {
        this._id = _id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.image = image;
        this.description = description;
        this.distributor = distributor;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
