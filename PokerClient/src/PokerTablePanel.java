import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PokerTablePanel extends JPanel{


    float potMoney = 0;
    boolean gameStarted = false;

    List<Player> tablePlayers;   // in  table
    List<Card> communityCards;
    List<Player> gamePlayers;

    Image backgroundImage;
    Image tableImage;

    Image image;

    public PokerTablePanel(){
        tablePlayers = new ArrayList<>();
        gamePlayers = new ArrayList<>();
        communityCards = new ArrayList<>();


        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/game_background.jpg"));
            tableImage = ImageIO.read(getClass().getResourceAsStream("/images/poker_table.png"));

        }catch (IOException e){e.printStackTrace();}


    }

    public void removePlayer(String name){
        List<Player> temp = new ArrayList<>(tablePlayers);
        for (Player p: temp){
            if (p.name.equalsIgnoreCase(name)){
                tablePlayers.remove(p);
                p.cards.clear();
                p.hasTurn = false;
                p.bet=0;
                p.folded=true;
                temp.clear();
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // background

        int w = super.getWidth();     // panel width
        int h = super.getHeight();    // panel height

        //image = ImageIO.read(new File(images_path+"game_background.jpg"));
        image = backgroundImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        g.drawImage(image, 0,0,null);

        //image = ImageIO.read(new File(images_path+"poker_table.png"));




        // table
        int sizex = w / 2;
        int sizey = (int) Math.round(h / 1.1);
        int x = w / 2 - (sizex / 2);
        int y = h / 2 - (sizey / 2);

        image = tableImage.getScaledInstance(sizex, sizey, Image.SCALE_SMOOTH);
        g.drawImage(image, x, y, null);

        int size = getWidth()/20;
        if(!tablePlayers.isEmpty()){
            g.setFont(new Font("Helvetica", Font.BOLD,  18));


            for (int i = 0; i< tablePlayers.size(); i++){
                w = image.getWidth(null);
                h = image.getHeight(null);
                int card_height = w / 10;
                int card_width = h / 10;
                switch (i){

                    case 0 -> {     // player 1
                        w = (int) Math.round(w/1.75);
                        h = (int) Math.round(h/5.3);

                        // name
                        g.setColor(Color.WHITE);
                        g.drawString(tablePlayers.get(i).name, w,h-10);

                        // bet
                        if (tablePlayers.get(i).bet > 0 && !tablePlayers.get(i).folded){ g.drawString("bet: "+ tablePlayers.get(i).bet,w+size+5  ,h+size); }


                        // avatar

                        if(tablePlayers.get(i).hasTurn ){ g.setColor(Color.green); }
                        else { g.setColor(Color.red); }
                        g.drawRect(w-2,h-2 , size+3,size+3);

                        g.drawImage(tablePlayers.get(i).avatar.getScaledInstance(size,size,Image.SCALE_SMOOTH), w,h,null);

                        if (tablePlayers.get(i).folded){
                            g.setColor(new Color( 255,255,255,50 ));
                            g.fillRect(w,h, size, size);
                        }


                        // cards

                            if(tablePlayers.get(i).cards.size()==2){

                                Image imagex = tablePlayers.get(i).cards.get(0).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                                g.drawImage(imagex,  w+size/2,h+size+10, null);
                                imagex = tablePlayers.get(i).cards.get(1).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                                g.drawImage(imagex,  w+size,h+size+10, null);
                            }


                    }
                    case 1 -> {     // player 2
                        w = (int) Math.round(w/1.045);
                        h = (int) Math.round(h/12.5);
                        g.setColor(Color.WHITE);
                        g.drawString(tablePlayers.get(i).name, w,h-5);

                        // bet
                        if (tablePlayers.get(i).bet > 0 && !tablePlayers.get(i).folded){ g.drawString("bet: "+ tablePlayers.get(i).bet,w+size+5  ,h+size); }

                        // avatar

                        if(tablePlayers.get(i).hasTurn ){ g.setColor(Color.green); }
                        else { g.setColor(Color.red); }
                        g.drawRect(w-2,h-2 , size+3,size+3);
                        g.drawImage(tablePlayers.get(i).avatar.getScaledInstance(size,size,Image.SCALE_SMOOTH), w,h,null);
                        if (tablePlayers.get(i).folded){
                            g.setColor(new Color( 255,255,255,50 ));
                            g.fillRect(w,h, size, size);
                        }


                        //cards
                        if(tablePlayers.get(1).cards.size()==2){

                            Image imagex = tablePlayers.get(i).cards.get(0).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w-7,h+size+10, null);
                            imagex = tablePlayers.get(i).cards.get(1).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w+(size/2)-7,h+size+10, null);
                        }


                    }
                    case 2 -> {     // player 3
                        w = (int) Math.round(w/0.765);
                        h = (int) Math.round(h/5.8);
                        g.setColor(Color.WHITE);
                        g.drawString(tablePlayers.get(i).name, w,h-10);
                        // bet
                        if (tablePlayers.get(i).bet > 0 && !tablePlayers.get(i).folded){ g.drawString("bet: "+ tablePlayers.get(i).bet,w+size+5  ,h+size); }


                        // avatar

                        if(tablePlayers.get(i).hasTurn ){ g.setColor(Color.green); }
                        else { g.setColor(Color.red); }
                        g.drawRect(w-2,h-2 , size+3,size+3);
                        g.drawImage(tablePlayers.get(i).avatar.getScaledInstance(size,size,Image.SCALE_SMOOTH), w,h,null);
                        if (tablePlayers.get(i).folded){
                            g.setColor(new Color( 255,255,255,50 ));
                            g.fillRect(w,h, size, size);
                        }

                        if(tablePlayers.get(i).cards.size()==2){
                            Image imagex = tablePlayers.get(i).cards.get(0).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w-size/2,h+size+10, null);
                            imagex = tablePlayers.get(i).cards.get(1).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w,h+size+10, null);
                        }






                    }
                    case 3 -> {     // player 4
                        w = (int) Math.round(w/0.779);
                        h = (int) Math.round(h/1.24);
                        g.setColor(Color.WHITE);
                        g.drawString(tablePlayers.get(i).name, w,h+size+15);
                        // bet
                        if (tablePlayers.get(i).bet > 0 && !tablePlayers.get(i).folded){ g.drawString("bet: "+ tablePlayers.get(i).bet,w+size+5  ,h+10); }


                        // avatar

                        if(tablePlayers.get(i).hasTurn )
                        { g.setColor(Color.green); }
                        else { g.setColor(Color.red); }
                        g.drawRect(w-2,h-2 , size+3,size+3);

                        g.drawImage(tablePlayers.get(i).avatar.getScaledInstance(size,size,Image.SCALE_SMOOTH), w,h,null);
                        if (tablePlayers.get(i).folded){
                            g.setColor(new Color( 255,255,255,50 ));
                            g.fillRect(w,h, size, size);
                        }

                        // cards
                        if(tablePlayers.get(i).cards.size()==2){
                            Image imagex = tablePlayers.get(i).cards.get(0).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w-size/2,h-(size+10), null);
                            imagex = tablePlayers.get(i).cards.get(1).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w,h-(size+10), null);
                        }

                    }
                    case 4 -> {     // player 5
                        w = (int) Math.round(w/1.045);
                        h = (int) Math.round(h/1.15);
                        g.setColor(Color.WHITE);
                        g.drawString(tablePlayers.get(i).name, w,h+size+15);
                        // bet
                        if (tablePlayers.get(i).bet > 0 && !tablePlayers.get(i).folded){ g.drawString("bet: "+ tablePlayers.get(i).bet,w+size+5  ,h+10); }


                        // avatar


                        if(tablePlayers.get(i).hasTurn ){ g.setColor(Color.green); }
                        else { g.setColor(Color.red); }
                        g.drawRect(w-2,h-2 , size+3,size+3);
                        g.drawImage(tablePlayers.get(i).avatar.getScaledInstance(size,size,Image.SCALE_SMOOTH), w,h,null);
                        if (tablePlayers.get(i).folded){
                            g.setColor(new Color( 255,255,255,50 ));
                            g.fillRect(w,h, size, size);
                        }

                        // cards
                        if(tablePlayers.get(i).cards.size()==2){
                            Image imagex = tablePlayers.get(i).cards.get(0).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w-7,h-(size+10), null);
                            imagex = tablePlayers.get(i).cards.get(1).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w+(size/2)-7,h-(size+10), null);
                        }

                    }
                    case 5 -> {     // player 6
                        w = (int) Math.round(w/1.67);
                        h = (int) Math.round(h/1.27);
                        g.setColor(Color.WHITE);
                        g.drawString(tablePlayers.get(i).name, w,h+size+15);
                        // bet
                        if (tablePlayers.get(i).bet > 0 && !tablePlayers.get(i).folded){ g.drawString("bet: "+ tablePlayers.get(i).bet,w+size+5  ,h+10); }

                        // avatar


                        if(tablePlayers.get(i).hasTurn ){ g.setColor(Color.green); }
                        else { g.setColor(Color.red); }
                        g.drawRect(w-2,h-2 , size+3,size+3);
                        g.drawImage(tablePlayers.get(i).avatar.getScaledInstance(size,size,Image.SCALE_SMOOTH), w,h,null);
                        if (tablePlayers.get(i).folded){
                            g.setColor(new Color( 255,255,255,50 ));
                            g.fillRect(w,h, size, size);
                        }

                        if(tablePlayers.get(i).cards.size()==2){
                            Image imagex = tablePlayers.get(i).cards.get(0).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w+size/2,h-(size+10), null);
                            imagex = tablePlayers.get(i).cards.get(1).image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                            g.drawImage(imagex,  w+size,h-(size+10), null);
                        }


                    }
                }
            }
        }
        if(gameStarted){
            // community cards and pot
            Image ccard;
            int pos = 0;
            g.setColor(Color.white);
            for (Card c: communityCards){
                w = image.getWidth(null);
                h = image.getHeight(null);
                int card_height = w / 10;
                int card_width = h / 10;

                w = (int) Math.round(w/1.3);
                h = (int) Math.round(h/2.1);

                ccard = c.image.getScaledInstance(card_width, card_height, Image.SCALE_SMOOTH);
                g.drawImage(ccard,  w+size/2+pos,h, null);
                pos += card_width;
        }


        // pot money

            w = image.getWidth(null);
            h = image.getHeight(null);
            w = (int) Math.round(w/1.3);
            h = (int) Math.round(h/2.1);
            g.drawString("pot: "+potMoney,w+(size*2),h-10);

        }


        g.dispose();
    }



}
