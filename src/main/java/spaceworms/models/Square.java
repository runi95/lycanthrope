package spaceworms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Square {
    private int number;
    private int posX;
    private int posY;
    private String name;
    private int wormhole;
    private String wormholeUrl;
    private Link[] links;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWormhole() {
        return wormhole;
    }

    public void setWormhole(int wormhole) {
        this.wormhole = wormhole;
    }

    public String getWormholeUrl() {
        return wormholeUrl;
    }

    public void setWormholeUrl(String wormholeUrl) {
        this.wormholeUrl = wormholeUrl;
    }

    public Link[] getLinks() {
        return links;
    }

    public void setLinks(Link[] links) {
        this.links = links;
    }
}
