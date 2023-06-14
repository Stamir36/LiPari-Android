package com.unesell.lipari;

public class Reward {

    private String userId;
    private String icon;
    private String name;
    private String info;

    public Reward(String userId, String icon, String name, String info) {
        this.userId = userId;
        this.icon = icon;
        this.name = name;
        this.info = info;
    }

    public String getUserId() {
        return userId;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }
}

