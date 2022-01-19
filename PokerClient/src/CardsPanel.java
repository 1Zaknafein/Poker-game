import javax.swing.*;
import java.awt.*;

public class CardsPanel extends JPanel {

    public Card[] cards;

    int card_width = 100;

    public CardsPanel(){
        setPreferredSize(new Dimension(200, 150));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());
        setBackground(null);
        cards = new Card[2];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (cards[0]!=null){
            Image image = cards[0].image.getScaledInstance(card_width, getHeight(), Image.SCALE_SMOOTH);
            g.drawImage(image, getWidth()/2-card_width ,0,null);

            image = cards[1].image.getScaledInstance(card_width, getHeight(), Image.SCALE_SMOOTH);
            g.drawImage(image, getWidth()/2, 0, null);



        }
        g.dispose();
    }
}
