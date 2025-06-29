package model;

import java.time.LocalDate;

public class Customer {
    private int id;
    private String name;
    private int wandId;
    private LocalDate purchaseDate;

    public Customer(int id, String name, int wandId, LocalDate purchaseDate) {
        this.id = id;
        this.name = name;
        this.wandId = wandId;
        this.purchaseDate = purchaseDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getWandId() { return wandId; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
}