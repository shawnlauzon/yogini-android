package com.santikama.yogini;

/**
 * Created by Admin on 5/10/16.
 */
public class Asana {
    private int id;
    private String name;
    private int order;
    private int time;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public int getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Asana{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", order=" + order +
                '}';
    }
}
