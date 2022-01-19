import javax.swing.*;
import java.awt.*;

public class Lobby_ProfilePanel extends JPanel {


    Player player;
    Font font;
    public Lobby_ProfilePanel(Player player){
        this.player = player;
        font = new Font("Helvetica", Font.PLAIN, 26);
        setLayout(null);
    }


    @Override
    protected void paintComponent(Graphics g) {

        g.setFont(font);
        int width = g.getFontMetrics().stringWidth(player.name);
        g.setColor(Color.white);
        g.drawString(player.name, 100 - width/2, 50);

        g.drawImage(player.avatar, 100 - (player.avatar.getWidth(null)/2), 60, null);

        g.drawLine(0,210,  200, 210);
        g.drawString("money: "+player.money, 10, 250);

    }
}
