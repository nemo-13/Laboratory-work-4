package model;

import java.time.LocalDate;

public class Supply {
    private int id;
    private String type;
    private LocalDate date;

    public Supply(int id, String type, LocalDate date) {
        this.id = id;
        this.type = type;
        this.date = date;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public LocalDate getDate() { return date; }
}