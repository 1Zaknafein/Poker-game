import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameImproved extends JPanel {


    // made up of:
    //                      - poker table                   center
    //                      - chat                          bottom left
    //                      - cards                         bottom
    //                      - actions                       bottom right
    //                      - table id & 'leave' button     top/top right


    Handler handler;
    private static Player player;
    JLabel playerMoney;
    List<Player> TablePlayers;
    List<Player> GamePlayers;
    GridBagConstraints c;
    JPanel chatPanel;
    CardsPanel cardsPanel;
    ActionsPanel actionsPanel;
    PokerTablePanel pokerTable;
    JPanel infoPanel;

    static float bet = 0;

    JLabel currentBet;

    JTextPane chat;
    JScrollPane chatScrollPane;
    SimpleAttributeSet attributeSet;
    Document doc;
    Color serverMessageColor;

    public GameImproved(Handler handler, Player p){

        setLayout(new BorderLayout());
        player = p;
        this.handler = handler;
        serverMessageColor = new Color(179,100,0);
        TablePlayers = new ArrayList<>();
        GamePlayers = new ArrayList<>();

        // these go inside bottom panel
        setChatPanel();
        setCardsPanel();
        setActionsPanel();

        setInfoPanel();         // top
        setPokerTablePanel();   // center
        setBottomPanel();

        revalidate();
        repaint();

        appendToChat("server", "hello");


    }

    private void setInfoPanel(){
        infoPanel = new JPanel();
        infoPanel.setLayout(null);

        infoPanel.setBackground(Color.darkGray);
        infoPanel.setPreferredSize(new Dimension(400, 100));

        JLabel playerName = new JLabel("name: "+player.name);
        playerMoney = new JLabel("money: "+player.money);
        currentBet = new JLabel("");

        Font font = new Font("Helvetica", Font.BOLD,  26);

        playerName.setForeground(Color.white);
        playerMoney.setForeground(Color.white);
        currentBet.setForeground(Color.white);

        playerName.setBounds(50, 10, 300,30);
        playerMoney.setBounds(50, 40, 300,30);
        currentBet.setBounds(400, 10, 300,30);

        currentBet.setFont(font);
        playerMoney.setFont(font);
        playerName.setFont(font);

        JButton leave = new JButton();
        leave.setSize(260,86);



        try{
            leave.setBounds( 500,1,260, 100 );
            //Image img = ImageIO.read(new File(ViewManager.imagesPath+"exit.png"));

            Image img  = ImageIO.read(getClass().getResourceAsStream("/images/exit.png"));

            img = img.getScaledInstance(leave.getWidth(),leave.getHeight(), Image.SCALE_SMOOTH );
            leave.setBackground(null);
            leave.setContentAreaFilled(false);
            leave.setMargin(null);
            leave.setBorder(BorderFactory.createEmptyBorder());
            leave.setIcon(new ImageIcon(img));

        }catch (IOException ignored){}

        infoPanel.add(playerName);
        infoPanel.add(playerMoney);
        infoPanel.add(currentBet);
        infoPanel.add(leave);

        add(infoPanel, BorderLayout.PAGE_START);

        infoPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                leave.setBounds(  infoPanel.getWidth()-leave.getWidth(), 0, leave.getWidth(), leave.getHeight());
                infoPanel.repaint();
            }
        });

        leave.addActionListener(e -> {
            pokerTable.gamePlayers.clear();
            pokerTable.communityCards.clear();
            pokerTable.tablePlayers.clear();
            pokerTable.gameStarted = false;
            pokerTable.potMoney = 0;
            player.bet = 0;
            player.cards.clear();

            player.folded = false;
            player.hasTurn = false;

            cardsPanel.cards[0] = null;
            cardsPanel.cards[1] = null;

            ViewManager.gui.changePanel(ViewManager.views.get(1));
            handler.sendToServer("game leave");
            handler.sendToServer("lobby update");
        });
    }

    private void setPokerTablePanel(){

        pokerTable = new PokerTablePanel();
        pokerTable.setPreferredSize(new Dimension(400,300));
        pokerTable.setMinimumSize(new Dimension(300,200));
        add(pokerTable, BorderLayout.CENTER);
        pokerTable.revalidate();
        pokerTable.repaint();

    }
    private void setBottomPanel(){
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.darkGray);
        panel.setBackground(new Color( 30,30,30 ));
        panel.setPreferredSize(new Dimension(400,150));
        panel.setOpaque(true);

        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_END;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.weightx = 0.3;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(chatPanel,c);

        c.gridx = 1;
        c.weightx = 0.2;

        c.anchor = GridBagConstraints.CENTER;
        panel.add(cardsPanel,c);

        c.gridx =2;
        c.weightx = 0.5;
        c.anchor = GridBagConstraints.LINE_END;

        panel.add(actionsPanel,c);

        add(panel, BorderLayout.PAGE_END);
    }
    private void setChatPanel(){
        chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(null);
        chatPanel.setPreferredSize(new Dimension(100, 150));
        chat = new JTextPane();

        JTextField inputField = new JTextField();
        inputField.setFont(new Font("Helvetica", Font.PLAIN, 16 ));

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (inputField.getText().length()>90){
                    JOptionPane.showMessageDialog(ViewManager.currentView, "Your message is too long!");
                }
                else if(inputField.getText().length()>0 && !inputField.getText().equalsIgnoreCase("")){
                try{
                        handler.sendToServer("game chat "+ inputField.getText());
                }catch (NullPointerException x){
                    JOptionPane.showMessageDialog(ViewManager.currentView, "Server connection is down");
                }
                inputField.setText("");
            }

            }
        });

        attributeSet = new SimpleAttributeSet();
        StyleConstants.setFontSize(attributeSet, 16);

        chat.setCharacterAttributes(attributeSet, true);
        chat.setText("");
        chat.setEditable(false);
        chat.setEditorKit(new CustomWrapper());
        chat.setBackground(new Color( 30,30,30 ));

        doc = chat.getStyledDocument();

        chatScrollPane = new JScrollPane(chat);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        JScrollBar v = chatScrollPane.getVerticalScrollBar();

        AdjustmentListener s = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                v.removeAdjustmentListener(this);
            }
        };
        v.addAdjustmentListener(s);

        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        inputField.setPreferredSize(new Dimension(chatPanel.getWidth(), 30));
        inputField.setMinimumSize(new Dimension(chatPanel.getWidth(), 30));

        chatPanel.add(inputField, BorderLayout.PAGE_END);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

    }
    private void setCardsPanel(){
        cardsPanel = new CardsPanel();
    }
    private void setActionsPanel(){
        actionsPanel = new ActionsPanel(handler);
    }

    public void gameEnded(String winner){
        if (winner!=null && !winner.equals("")){

            for (Player p: pokerTable.tablePlayers){
                if (p.name.equals(winner)){
                    p.money += pokerTable.potMoney;
                }
            }
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ViewManager.currentView, winner + " has won!"));

        }

        cardsPanel.cards[0] = null;
        cardsPanel.cards[1] = null;
        pokerTable.gameStarted = false;
        pokerTable.gamePlayers.clear();
        //pokerTable.tablePlayers.clear();
        pokerTable.communityCards.clear();

        pokerTable.potMoney = 0;
        for (Player p: pokerTable.tablePlayers){
            p.bet = 0;
            p.cards.clear();
            p.hasTurn = false;
            p.folded = false;
        }


        appendToChat("server" , "\n");

        SwingUtilities.invokeLater(() -> {
            for (JButton b: actionsPanel.buttons){
                b.setEnabled(false);
            }
            currentBet.setText("");
            repaint();
        });




    }

    public void gameEnded(String[] winners, float prize){
        StringBuilder sb = new StringBuilder();

        for (Player p: pokerTable.tablePlayers){
            for (String winner: winners){
                if (p.name.equals(winner)){
                    p.money += prize;
                    break;
                }
            }
        }

        for (String s:winners){
            sb.append(s).append(", ");
        }
        sb.setLength(sb.length()-3);




        cardsPanel.cards[0] = null;
        cardsPanel.cards[1] = null;
        pokerTable.gameStarted = false;
        pokerTable.gamePlayers.clear();
        pokerTable.communityCards.clear();

        pokerTable.potMoney = 0;
        for (Player p: pokerTable.tablePlayers){
            p.bet = 0;
            p.cards.clear();
            p.hasTurn = false;
            p.folded = false;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (JButton b: actionsPanel.buttons){
                    b.setEnabled(false);
                }
                currentBet.setText("");
                JOptionPane.showMessageDialog(ViewManager.currentView,  sb.toString()+ " won!");
                repaint();
            }
        });
        sb.setLength(0);

        appendToChat("server" , "\n");


    }

    public void appendToChat(String name, String message){

        if (message.length()==0) return;

        SwingUtilities.invokeLater(() -> {
            if (name.equalsIgnoreCase("server")){

                StyleConstants.setBold(attributeSet, true);
                StyleConstants.setForeground(attributeSet, serverMessageColor);
                try{
                    doc.insertString(doc.getLength(), message+"\n" , attributeSet);
                }catch (BadLocationException e){e.printStackTrace();}
            }
            else {
                StyleConstants.setBold(attributeSet, true);
                StyleConstants.setForeground(attributeSet, Color.white);
                try{
                    doc.insertString(doc.getLength(), name+": " , attributeSet);

                    StyleConstants.setBold(attributeSet, false);
                    doc.insertString(doc.getLength(),message+"\n" , attributeSet);

                }catch (BadLocationException e){e.printStackTrace();}
            }
        });



    }
    static class CustomWrapper extends StyledEditorKit {

        private final ViewFactory factory = new CustomViewFactory();

        @Override
        public ViewFactory getViewFactory () {
            return factory;
        }
    }
    static class CustomViewFactory implements ViewFactory {

        public View create(Element e) {
            String name = e.getName();

            if (name != null) {
                switch (name) {
                    case AbstractDocument.ContentElementName -> {   return new WrapText(e); }
                    case StyleConstants.ComponentElementName -> {   return new ComponentView(e); }
                    case AbstractDocument.ParagraphElementName -> { return new ParagraphView(e); }
                    case StyleConstants.IconElementName -> {        return new IconView(e); }
                    case AbstractDocument.SectionElementName -> {   return new BoxView(e, View.Y_AXIS); }
                }
            }
            return new LabelView(e);
        }

        static class WrapText extends LabelView {

            public WrapText(Element e) {
                super(e);
            }

            @Override
            public float getMinimumSpan(int i) {
                return switch (i) {
                    case View.X_AXIS -> 0;
                    case View.Y_AXIS -> super.getMinimumSpan(i);
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                };
            }
        }

    }


    public Player getPlayer(String name){
        for (Player p: pokerTable.tablePlayers){
            if (p.name.equals(name)) return p;
        }
        return null;
    }
}
