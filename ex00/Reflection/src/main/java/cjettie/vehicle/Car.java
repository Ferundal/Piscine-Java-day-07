package cjettie.vehicle;

import java.util.StringJoiner;

public class Car {
    private int maxSpeed;
    private int gasTank;
    private String color;
    public Car() {
        this.maxSpeed = 0;
        this.gasTank = 0;
        color = "white";
    }

    public Car(int maxSpeed, int gasTank, String color) {
        this.maxSpeed = maxSpeed;
        this.gasTank = gasTank;
        this.color = color;
    }

    public void changeColor(String color) {
        this.color = color;
    }
    @Override
    public String toString() {
        return new StringJoiner(", ", Car.class.getSimpleName() + "[", "]")
                .add("maxSpeed='" + maxSpeed + "'")
                .add("gasTank='" + gasTank + "'")
                .add("color=" + color)
                .toString();
    }
}
