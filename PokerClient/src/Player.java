import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player {

    public String name;
    float money = 0;
    List<Card> cards;
    float bet = 0;
    public Image avatar;
    boolean hasTurn = false;
    boolean folded = false;

    public Player(String name){
        this.name = name;
        cards=new ArrayList<>();

        try {
            //System.out.println(cl.getResource("res/images/default_avatar.jpg"));
            avatar = ImageIO.read(getClass().getResourceAsStream("/images/default_avatar.jpg"));

            //avatar = ImageIO.read(new File(String.valueOf(cl.getResource("res/images/default_avatar.jpg"))));
            //avatar = ImageIO.read(new File(System.getProperty("user.dir") + "\\src\\res\\images\\default_avatar.jpg"));

            avatar = avatar.getScaledInstance( 128,128, Image.SCALE_SMOOTH);
        }catch (IOException e){
            System.out.println("unable to load default avatar");
        }
    }

    public void setMoney(float amount){
        money = amount;
    }

}
