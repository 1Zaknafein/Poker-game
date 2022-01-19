import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlayerListPanel extends JPanel {

    List<Player>players;
    private JList plist;
    public DefaultListModel<String> playerList;
    private Font font = new Font("Arial", Font.BOLD, 20);
    public PlayerListPanel(List<Player> p){

        setPreferredSize(new Dimension(200,400));
        players = new ArrayList<>(p);
        playerList = new DefaultListModel<>();



        if (players.size() > 0) {
            Collections.sort(players, new Comparator<Player>() {
                @Override
                public int compare(Player p1,  Player p2) {
                    return p1.name.compareTo(p2.name);
                }
            });
        }


        for (Player pl:players){
            playerList.addElement(pl.name);
        }

        plist = new JList(playerList);
        //plist.setPreferredSize(new Dimension(180,200));
        plist.setBackground(null);
        plist.setFont(font);

        setBackground(null);
        JLabel l = new JLabel("Online players");
        l.setForeground(Color.white);
        l.setFont(font);

        add(l);

        JScrollPane sp = new JScrollPane(plist);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setPreferredSize(new Dimension(180,400));
        add(sp);

        plist.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //todo show profile, currently playing info ?

                if(SwingUtilities.isLeftMouseButton(e)){

                    if(e.getClickCount()==2 && !e.isConsumed()){
                        String name = (String) plist.getModel().getElementAt(plist.locationToIndex(e.getPoint()));
                        System.out.println(name);

                    }
                }

            }
        });

        revalidate();
        repaint();
    }

    public void addPlayer(Player p){

        players.add(p);
        if (players.size() > 0) {
            Collections.sort(players, new Comparator<Player>() {
                @Override
                public int compare(Player p1,  Player p2) {
                    return p1.name.compareTo(p2.name);
                }
            });
        }

        playerList.clear();

        for (Player pl:players){
            playerList.addElement(pl.name);
        }

        plist.revalidate();
        plist.repaint();
    }

    public void removePlayer(Player p){
        players.remove(p);
        if (players.size() > 0) {
            Collections.sort(players, new Comparator<Player>() {
                @Override
                public int compare(Player p1,  Player p2) {
                    return p1.name.compareTo(p2.name);
                }
            });
        }

        playerList.clear();

        for (Player pl:players){
            playerList.addElement(pl.name);
        }

        plist.revalidate();
        plist.repaint();


    }

    void removeAllPlayers(){
        playerList.removeAllElements();
        players.clear();
    }
}
