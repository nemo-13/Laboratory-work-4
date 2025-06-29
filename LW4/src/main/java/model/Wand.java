package model;

import java.time.LocalDate;

public class Wand {
    private int id;
    private int coreId;
    private int woodId;
    private double price;
    private LocalDate creationDate;
    private boolean sold;

    public Wand(int id, int coreId, int woodId, double price, LocalDate creationDate, boolean sold) {
        this.id = id;
        this.coreId = coreId;
        this.woodId = woodId;
        this.price = price;
        this.creationDate = creationDate;
        this.sold = sold;
    }

    public int getId() { return id; }
    public int getCoreId() { return coreId; }
    public int getWoodId() { return woodId; }
    public double getPrice() { return price; }
    public LocalDate getCreationDate() { return creationDate; }
    public boolean isSold() { return sold; }
    public void setSold(boolean sold) { this.sold = sold; }
}