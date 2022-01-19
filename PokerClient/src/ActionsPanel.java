import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActionsPanel extends JPanel implements ActionListener {

    public List<JButton> buttons;
    private JButton betButton;
    private JButton callButton;
    private JButton checkButton;
    private JButton foldButton;
    private JButton raiseButton;
    SliderPanel betSlider;
    SliderPanel raiseSlider;
    Handler handler;


    private Image betButtonImage;
    private Image callButtonImage;
    private Image checkButtonImage;
    private Image foldButtonImage;
    private Image raiseButtonImage;
    private final String[] buttonNames = new String[5];

    public ActionsPanel(Handler handler){

        this.handler = handler;
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBackground(null);
        setPreferredSize(new Dimension(300, 150));
        setMinimumSize(getPreferredSize());
        setMaximumSize(getPreferredSize());

        betSlider = new SliderPanel(this, 0);
        raiseSlider = new SliderPanel(this, 1);

        setupButtons();

        add(checkButton);
        add(callButton);
        add(betButton);
        add(raiseButton);
        add(foldButton);


        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                int size = getWidth();

                if (size<430  && buttons.get(0).getSize().height>75){
                    for (int i=0; i<buttons.size();i++){
                        buttons.get(i).setSize(70,70);
                        //Image img = ImageIO.read(new File(ViewManager.imagesPath+ buttonNames[i] +".png"));
                        //Image img = ImageIO.read(getClass().getResourceAsStream("/images/"+buttonNames[i]+".png"));
                        Image img = getButtonImage(buttonNames[i]);

                        assert img != null;
                        img = img.getScaledInstance(buttons.get(i).getWidth(), buttons.get(i).getHeight(), Image.SCALE_SMOOTH );
                        buttons.get(i).setIcon(new ImageIcon(img));
                    }
                }

                if ((size<528 && size>430) && (buttons.get(0).getSize().height>80 || buttons.get(0).getSize().height < 79)){
                    for (int i=0; i<buttons.size();i++){
                        buttons.get(i).setSize(80,80);
                        //Image img = ImageIO.read(new File(ViewManager.imagesPath+ buttonNames[i] +".png"));
                        //Image img = ImageIO.read(getClass().getResourceAsStream("/images/"+buttonNames[i]+".png"));
                        Image img = getButtonImage(buttonNames[i]);

                        assert img != null;
                        img = img.getScaledInstance(buttons.get(i).getWidth(), buttons.get(i).getHeight(), Image.SCALE_SMOOTH );
                        buttons.get(i).setIcon(new ImageIcon(img));
                    }
                }

                else if(buttons.get(0).getSize().height<101 && size>725){

                    for (int i=0; i<buttons.size();i++){
                        buttons.get(i).setSize(140,140);
                        //Image img = ImageIO.read(new File(ViewManager.imagesPath+buttonNames[i]+".png"));
                        //Image img = ImageIO.read(getClass().getResourceAsStream("/images/"+buttonNames[i]+".png"));
                        Image img = getButtonImage(buttonNames[i]);

                        assert img != null;
                        img = img.getScaledInstance(buttons.get(i).getWidth(), buttons.get(i).getHeight(), Image.SCALE_SMOOTH );
                        buttons.get(i).setIcon(new ImageIcon(img));
                    }

                }
                else if((size >528 && size <725) && (buttons.get(0).getSize().height > 101 || buttons.get(0).getSize().height < 95) ){

                    for (int i=0; i<buttons.size();i++){
                        buttons.get(i).setSize(100,100);
                        //Image img = ImageIO.read(new File(ViewManager.imagesPath+ buttonNames[i] +".png"));
                        //Image img = ImageIO.read(getClass().getResourceAsStream("/images/"+buttonNames[i]+".png"));
                        Image img = getButtonImage(buttonNames[i]);

                        assert img != null;
                        img = img.getScaledInstance(buttons.get(i).getWidth(), buttons.get(i).getHeight(), Image.SCALE_SMOOTH );
                        buttons.get(i).setIcon(new ImageIcon(img));
                    }
                }


                revalidate();
                repaint();
            }
        });


    }

    private void setupButtons(){

        buttons = new ArrayList<>();
        buttons.add(checkButton = new JButton("check"));
        buttons.add(callButton = new JButton("call"));
        buttons.add(betButton = new JButton("bet"));
        buttons.add(raiseButton = new JButton("raise"));
        buttons.add(foldButton = new JButton("fold"));

        try {
            betButtonImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/bet.png")));
            checkButtonImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/check.png")));
            callButtonImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/call.png")));
            raiseButtonImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/raise.png")));
            foldButtonImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/fold.png")));

        }catch (IOException e){e.printStackTrace();}

        int i = 0;
        for (JButton b: buttons){
            buttonNames[i++] = b.getActionCommand();
            b.addActionListener( this );

            b.setSize(140,140);
            //Image img = ImageIO.read(new File(ViewManager.imagesPath+b.getText()+".png"));
            //Image img = ImageIO.read(getClass().getResourceAsStream("/images/"+b.getText()+".png"));
            Image img = getButtonImage(b.getText());

            img = img.getScaledInstance(b.getWidth(), b.getHeight(), Image.SCALE_SMOOTH );
            b.setBackground(null);
            b.setContentAreaFilled(false);
            b.setMargin(null);
            b.setText("");
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setIcon(new ImageIcon(img));
            b.setEnabled(false);

        }
    }

    private Image getButtonImage(String button){
        switch (button){
            case "check" -> {return checkButtonImage;}
            case "call" -> {return callButtonImage;}
            case "bet" -> {return betButtonImage;}
            case "raise" -> {return raiseButtonImage;}
            case "fold" -> {return foldButtonImage;}
            default -> {return null;}
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try{
            if(e.getSource()==betButton){
                for (JButton b: buttons){ remove(b); }
                betSlider.slider.setMinimum((int)GameImproved.bet+1);
                betSlider.slider.setMaximum((int)GameImproved.bet+10);
                betSlider.slider.setValue((int)GameImproved.bet+5);

                add(betSlider);
                revalidate();
                repaint();

            }
            else if(e.getSource()==callButton){     handler.sendToServer("game action call");}
            else if(e.getSource()==checkButton){    handler.sendToServer("game action check");}
            else if(e.getSource()==foldButton){     handler.sendToServer("game action fold");}

            else if(e.getSource()==raiseButton){
                for (JButton b: buttons){ remove(b); }

                raiseSlider.slider.setMinimum((int)GameImproved.bet+1);
                raiseSlider.slider.setMaximum((int)GameImproved.bet+10);
                raiseSlider.slider.setValue((int)GameImproved.bet+5);

                add(raiseSlider);
                revalidate();
                repaint();
            }

        }catch (NullPointerException h){ JOptionPane.showMessageDialog(ViewManager.currentView, "Server connection is down"); }

        buttons.forEach(x -> x.setEnabled(false));

    }


    private class SliderPanel extends JPanel{
        JSlider slider;
        ActionsPanel parent;
        public SliderPanel(ActionsPanel parent, int i){


            this.parent = parent;
            JButton send = new JButton();

            setBackground(null);
            setPreferredSize(new Dimension(300,150));
            setLayout(null);

            parent.setLayout(new FlowLayout(FlowLayout.CENTER));
            slider = new JSlider((int)GameImproved.bet+1,(int)GameImproved.bet+10, (int)GameImproved.bet+5);
            slider.setForeground(Color.orange);
            slider.setBackground(null);

            slider.setOrientation(JSlider.HORIZONTAL);
            slider.setPaintTicks( true );
            slider.setMajorTickSpacing(10);

            slider.setBounds(60, 20, 160, 30);
            if (i==0){
                send.setText("BET "+slider.getValue());
                send.setBounds(60,60, 150, 80);

            }
            else {
                send.setText("RAISE TO "+slider.getValue());
                send.setBounds(60,60, 230, 80);
            }
            slider.addChangeListener(e -> {
                if (i==0){
                    send.setText("BET "+slider.getValue());
                }
                else {
                    send.setText("RAISE TO "+slider.getValue());
                }
                repaint();
            });

            send.setFont(new Font("Helvetica", Font.BOLD , 24));
            send.setBackground( Color.orange );
            send.addActionListener(e -> {
                if (i==0){
                    handler.sendToServer("game action bet "+slider.getValue());
                }
                else {
                    handler.sendToServer("game action raise "+slider.getValue());
                }

                parent.remove(this);
                setLayout(new FlowLayout(FlowLayout.RIGHT));
                for (JButton b: parent.buttons){
                    parent.add(b);
                }
                parent.revalidate();
                parent.repaint();
            });

            add(slider);
            add(send);

            revalidate();
            repaint();

        }

    }
}
