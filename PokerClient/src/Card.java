import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Card {

    String rank;
    char suit;
    Image image;

    public Card(String rank, String suit){

        switch (suit.toLowerCase()){
            case "diamonds" -> this.suit  = '♦';
            case "hearts" -> this.suit  = '♥';
            case "clubs" -> this.suit  = '♣';
            case "spades" -> this.suit  = '♠';
        }
        this.rank = rank;

        try{
            //image = ImageIO.read(new File(ViewManager.imagesPath+rank+suit.charAt(0)+".png"));
            image = ImageIO.read(getClass().getResourceAsStream("/images/"+rank+suit.charAt(0)+".png"));


        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public Card(String n){

        if (n.equals("hidden")){
            try{
                //image = ImageIO.read(new File(ViewManager.imagesPath+"card_back.png"));
                image = ImageIO.read(getClass().getResourceAsStream("/images/card_back.png"));

            }catch (IOException e){
                e.printStackTrace();
            }
        }


    }
    @Override
    public String toString() {
        return rank+ " " +suit;
    }
}