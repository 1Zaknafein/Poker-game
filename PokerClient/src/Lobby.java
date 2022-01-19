import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;


public class Lobby extends JPanel {


    Handler handler;
    Player player;
    //JPanel profilePanel = new JPanel();
    Lobby_ProfilePanel profilePanel;
    JPanel tablePanel = new JPanel(new GridBagLayout());
    JPanel playerListPanel = new JPanel();
    JLabel playerMoneyLabel = new JLabel();

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1,5 ));
    JPanel bottomPanel = new JPanel(new BorderLayout(0,0));

    JButton exitBtn = new JButton("EXIT");
    JButton settingsBtn = new JButton("SETTINGS");
    private List<Player> playerList;

    JTable table;
    public DefaultTableModel tableModel;
    // Lobby pane -> 2 panels (top, bottom)
                               // top -> buttons EXIT, SETTINGS...
                                // bottom -> 3 panels -> profile (left), table (center), player list (right)

    public Lobby(Handler h, Player p) {

        ViewManager.gui.setMinimumSize(new Dimension(1280,720));
        ViewManager.gui.setPreferredSize(new Dimension(1600,900));
        ViewManager.gui.setSize(1600, 900);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        ViewManager.gui.setLocation(screenSize.width/2- (ViewManager.gui.getSize().width/2), screenSize.height/2-ViewManager.gui.getSize().height/2);

        ViewManager.gui.pack();
        handler = h;
        player = p;
        setLayout(new BorderLayout());
        tablePanel.setBackground(Color.DARK_GRAY);

        List<Table> tables = new ArrayList<>();

        setupTableList(tables);
        setupProfilePanel();
        setPlayerList();

        exitBtn.setFocusable(false);
        exitBtn.setMargin(new Insets(20,30,20,30));
        exitBtn.addActionListener(e -> {
            ViewManager.gui.dispatchEvent(new WindowEvent(ViewManager.gui, WindowEvent.WINDOW_CLOSING));
        });

        settingsBtn.setFocusable(false);
        settingsBtn.setMargin(new Insets(20,25,20,25));

        topPanel.setBackground(Color.darkGray);
        topPanel.setPreferredSize(new Dimension(0,90));
        topPanel.add(settingsBtn);
        topPanel.add(exitBtn);
        topPanel.setBorder(BorderFactory.createBevelBorder(1));

        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setPreferredSize(new Dimension(600,900));
        bottomPanel.add(profilePanel,BorderLayout.LINE_START);
        bottomPanel.add(playerListPanel,BorderLayout.LINE_END);

        profilePanel.setBackground(Color.darkGray);
        profilePanel.setPreferredSize(new Dimension(200, ViewManager.gui.getHeight()));

        playerListPanel.setBackground(Color.darkGray);
        playerListPanel.setPreferredSize(new Dimension(200, 110));

        add(topPanel, BorderLayout.PAGE_START);
        add(bottomPanel, BorderLayout.CENTER);


        loadIcons();
        revalidate();
        repaint();

    }


    private void setupProfilePanel(){
        profilePanel = new Lobby_ProfilePanel(player);
        profilePanel.add(playerMoneyLabel);
    }


    public void setupTableList(List<Table> tables){
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFocusable(false);
        table.setCellSelectionEnabled(false);

        tableModel.addColumn("Table");
        tableModel.addColumn("Players");
        tableModel.addColumn("Bet");
        tableModel.addColumn("");

        table.setRowHeight(50);
        table.setFont(new Font("Arial", Font.PLAIN, 20));
        table.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader h = table.getTableHeader();
        h.setPreferredSize(new Dimension(0,70));
        h.setFont(new Font("Arial", Font.PLAIN, 20));
        h.setBackground(Color.orange);
        h.setBorder(BorderFactory.createEmptyBorder());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        centerRenderer.setBackground(Color.lightGray);
        centerRenderer.setBorder(BorderFactory.createEmptyBorder());

        if(tables!=null && tables.size()>0){
            for (Table t:tables){
                tableModel.addRow(new Object[]{t.id, t.players+"/"+t.maxPlayers, t.minBet, "Join"});
            }
        }

        for (int i=0;i<table.getColumnCount();i++){
            table.getColumnModel().getColumn(i).setCellRenderer( centerRenderer );
        }

        JScrollPane sp = new JScrollPane(table);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)){
                    int column = table.columnAtPoint(e.getPoint());

                    if (column==3){
                        int row = table.rowAtPoint(e.getPoint());
                        try {

                            handler.sendToServer("joinTable " + table.getValueAt(row, 0));      //send 'table id' to the server
                        }catch (Exception ex){
                            JOptionPane.showMessageDialog(ViewManager.currentView, "unable to join");
                        }
                    }
                }
            }

        });

        // todo mouse motion listener for highlighting table rows - need custom cell renderer

/*        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                System.out.println(row);
            }
        });*/

        bottomPanel.add(sp);
        validate();
        repaint();

    }
    public void updateTableList(Table t){

        if (t!=null){
            if (t.id > table.getRowCount()){
                // id too high
                return;
            }

            if (t.id == table.getRowCount()){
                // not in the table, need to be added
                tableModel.addRow(new Object[]{t.id, t.players+"/"+t.maxPlayers, t.minBet, "Join"});
            }
            else {
                // in the table, value need to be changed
                tableModel.setValueAt(t.players+"/"+t.maxPlayers, t.id, 1);
            }
            table.revalidate();
            table.repaint();
        }

    }

    public void updateTableList(int id, int n){

        if (Table.tableList.size()>0){
            Table t = Table.tableList.get(id);
            t.players = n;

            tableModel.setValueAt(t.players+"/"+t.maxPlayers, t.id, 1);
            table.revalidate();
            table.repaint();
        }

    }

    private void setPlayerList(){
        playerList = new ArrayList<>();
        playerListPanel.add(new PlayerListPanel(playerList));
        playerListPanel.revalidate();
        playerListPanel.repaint();
    }
    public void addPlayerToOnlineList(Player p){
        for (Component c: playerListPanel.getComponents()){
            if (c instanceof PlayerListPanel){
                ((PlayerListPanel) c).addPlayer(p);
                break;
            }
        }
        playerListPanel.revalidate();
        playerListPanel.repaint();
    }

    public void removePlayerFromOnlineList(String name){
        for (Component c: playerListPanel.getComponents()){
            if (c instanceof PlayerListPanel e){
                for (Player p: e.players){
                    if (p.name.equals(name)){
                        e.removePlayer(p);
                        break;
                    }
                }
                break;
            }
        }
        playerListPanel.revalidate();
        playerListPanel.repaint();
    }

    public void clear(){
        tableModel.setRowCount(0);
        table.revalidate();
        table.repaint();

        for (Component c: playerListPanel.getComponents()){
            if (c instanceof PlayerListPanel e) {
                e.removeAllPlayers();
                break;
            }
        }

    }

    private void loadIcons(){


        try {


            settingsBtn.setSize(80,80);
            exitBtn.setSize(180,70);

            //Image img = ImageIO.read(Objects.requireNonNull(getClass().getResource("/images/settings.png")));
            Image img = ImageIO.read(getClass().getResourceAsStream("/images/settings.png"));


            img = img.getScaledInstance(settingsBtn.getWidth(), settingsBtn.getHeight(), Image.SCALE_SMOOTH );
            settingsBtn.setBackground(null);
            settingsBtn.setMargin(null);
            settingsBtn.setText("");
            settingsBtn.setContentAreaFilled(false);

            settingsBtn.setBorder(BorderFactory.createEmptyBorder());
            settingsBtn.setIcon(new ImageIcon(img));

            img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/exit.png")));

            img = img.getScaledInstance(exitBtn.getWidth(), exitBtn.getHeight(), Image.SCALE_SMOOTH );
            exitBtn.setBackground(null);
            exitBtn.setMargin(null);
            exitBtn.setContentAreaFilled(false);
            exitBtn.setText("");
            exitBtn.setBorder(BorderFactory.createEmptyBorder());
            exitBtn.setIcon(new ImageIcon(img));

        } catch (Exception ex) {
            ex.printStackTrace();
            exitBtn.setSize(180,0);
            exitBtn.setBackground(new Color(200,100,20));
        }
    }
}