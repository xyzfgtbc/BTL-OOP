package com.cstore.model;

/** Lop Employee - Quan ly thong tin nhan vien. */
public class Employee {
    private String id;
    private String name;
    private String position;
    private double salary;

    public Employee(String id, String name, String position, double salary) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.salary = salary;
    }

    public Object[] toRowData() {
        return new Object[]{id, name, position, String.format("%,.0f", salary)};
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPosition() { return position; }
    public double getSalary() { return salary; }
}
