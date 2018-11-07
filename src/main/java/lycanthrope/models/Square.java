package lycanthrope.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table
public class Square {

    private int number;
    private int posX;
    private int posY;
    private String name;
    private int wormhole;
    private boolean isWormhole;
    private String wormholeUrl;

    @Transient
    private Link[] links;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    public int getId() { return this.id; }

    public int getNumber() { return number; }

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

    public Board getBoard() { return board; }

    public void setBoard(Board board) { this.board = board; }

    public boolean getIsWormhole() { return isWormhole; }

    public boolean isWormhole() { return isWormhole; }

    public void setIsWormhole(boolean isWormhole) { this.isWormhole = isWormhole; }
}
