import javax.swing.*;
import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;

public class Table {

    static List<Table> tableList = new ArrayList<>();

    int id;
    int players;
    int maxPlayers;
    float minBet;

    public Table(int id, int players, int maxPlayers, float minBet){
        this.id = id;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.minBet = minBet;
        //tableList.add(this);


        SwingUtilities.invokeLater(() -> ((Lobby) ViewManager.currentView).updateTableList(this));

    }

    public String getTableInfo(){
        return "table "+id + ": "+ players+"/"+maxPlayers + "  minBet: "+minBet;
    }
}
