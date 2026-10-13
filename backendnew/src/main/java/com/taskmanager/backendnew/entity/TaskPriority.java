package com.taskmanager.backendnew.entity;

public enum TaskPriority {
    LOW(1, 0), MEDIUM(2, 24), HIGH(3, 72), URGENT(4, 120);

    private final int weight;
    private final int bonusHours;

    TaskPriority(int weight, int bonusHours) {
        this.weight = weight;
        this.bonusHours = bonusHours;
    }

    public int getWeight() {
        return weight;
    }

    public int getBonusHours() {
        return bonusHours;
    }

}
