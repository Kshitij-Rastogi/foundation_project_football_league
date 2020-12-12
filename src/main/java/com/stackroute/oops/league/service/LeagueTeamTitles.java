package com.stackroute.oops.league.service;

/**
 * Enum to store the four team titles
 * Contains one field description and a parameterized constructor to initialize it
 * Modify this code by adding description to each enum constants
 */
public enum LeagueTeamTitles {
    HIPHOP("Hiphop"), 
    WIN2WIN("Win2Win"),
    HAPPYFEET("Happy Feet"), 
    LUCKYSTRIKE("Lucky Strike");

    private String teamTitle;

    LeagueTeamTitles(String teamTitle){
        this.teamTitle = teamTitle;
    }

    public String getTeamTitle() {
        return teamTitle;
    }

    public void setTeamTitle(String teamTitle) {
        this.teamTitle = teamTitle;
    }

}
