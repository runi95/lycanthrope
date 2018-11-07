
package lycanthrope.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table
public class Board {

    private String name;
    private String description;
    private String size;
    private int dimX;
    private int dimY;
    private int start;
    private int goal;

    @Id
    private int id;

    @JsonBackReference
    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Lobby lobby;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("number ASC")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Square> squares;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getDimX() { return dimX; }
    public void setDimX(int dimX) { this.dimX = dimX; }

    public int getDimY() { return dimY; }
    public void setDimY(int dimY) { this.dimY = dimY; }

    public int getStart() { return start; }
    public void setStart(int start) { this.start = start; }

    public int getGoal() { return goal; }
    public void setGoal(int goal) { this.goal = goal; }

    public Lobby getLobby() { return lobby; }
    public void setLobby(Lobby lobby) { this.lobby = lobby; }

    public List<Square> getSquares() { return squares; }
    public void setSquares(List<Square> squares) { this.squares = squares; }
}