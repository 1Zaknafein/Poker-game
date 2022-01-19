import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Handler implements AutoCloseable{


    String serverIP;
    int port;
    private Scanner reader;
    private PrintWriter writer;
    Player player;
    boolean gameOver = false;
    public static Handler handler;
    StringBuilder sb = new StringBuilder();
    Socket socket;

    public Handler(int port, String serverIPAddress, Player player){
        this.port = port;
        this.serverIP = serverIPAddress;
        this.player = player;
        handler = this;
        try {
            socket = new Socket(serverIP, port);

            // for reading data from server
            reader = new Scanner(socket.getInputStream());

            // for writing data to server
            writer = new PrintWriter(socket.getOutputStream(), true);

            sendToServer("player name "+player.name); // send first command to the server

            Thread listener = new Thread(() -> {
                try {
                    while(!gameOver){

                        readServerResponse();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            listener.start();

        }catch (IOException e){
            SwingUtilities.invokeLater(() ->JOptionPane.showMessageDialog(ViewManager.currentView, "Unable to connect to the server"));

        }
        catch (IllegalStateException x){
            x.printStackTrace();
        }
    }

    public synchronized void sendToServer(String command){
        //System.out.println("client -> server ("+command+")");
        writer.println(command);
    }

    private void readServerResponse(){
        String line;
        String[] command;
        try{

            if(reader.hasNext()) {
                line = reader.nextLine();
                command = line.split(" ");

                //System.out.println("server -> client "+ Arrays.toString(command));
                switch (command[0]) {

                    case "player":
                        switch (command[1]){

                            case "allowed" -> {

                                    ViewManager.views.add(new Lobby(handler, player));
                                    ViewManager.views.add(new GameImproved(handler, player));

                                    ViewManager.gui.add(ViewManager.views.get(1));
                                    ViewManager.gui.add(ViewManager.views.get(2));

                                    ViewManager.currentView = ViewManager.views.get(1);
                                    ViewManager.gui.changePanel(ViewManager.views.get(1));

                            }

                            case "exists" -> {
                                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ViewManager.currentView, "Name already taken"));
                            }

                            case "money" -> {
                                float money = Float.parseFloat(command[2]);
                                player.setMoney(money);

                                if (ViewManager.currentView instanceof GameImproved game){
                                    SwingUtilities.invokeLater(() -> game.playerMoney.setText("money: "+player.money));

                                }
                                SwingUtilities.invokeLater(() -> ViewManager.currentView.repaint());

                            }
                        }
                        break;
                    case "table":
                        if(ViewManager.currentView == ViewManager.views.get(1)) {

                            switch (command[1]) {

                                case "failed" -> {
                                    SwingUtilities.invokeLater(()-> JOptionPane.showMessageDialog(ViewManager.currentView, "unable to join"));
                                    System.out.println("Unable to join the table");
                                }
                                case "success" -> {
                                    SwingUtilities.invokeLater(()->ViewManager.gui.changePanel(ViewManager.views.get(2)));
                                }
                                case "initial" -> {


                                    // table -> id -> players in the table -> max players -> min bet

                                    int id = Integer.parseInt(command[2]);
                                    int players = Integer.parseInt(command[3]);
                                    int maxPlayers = Integer.parseInt(command[4]);
                                    float minBet = Float.parseFloat(command[5]);

                                    Table.tableList.add(new Table(id, players, maxPlayers, minBet));
                                    SwingUtilities.invokeLater(() -> {
                                        ViewManager.currentView.revalidate();
                                        ViewManager.currentView.repaint();
                                    });
                                }
                                case "update" -> {

                                    if (ViewManager.currentView instanceof Lobby lobby){
                                        int id = Integer.parseInt(command[2]);
                                        int players = Integer.parseInt(command[3]);
                                        lobby.updateTableList(id,players);

                                        ViewManager.currentView.revalidate();
                                        ViewManager.gui.revalidate();
                                        ViewManager.currentView.repaint();
                                        ViewManager.gui.repaint();

                                    }



                                }

                            }

                        }
                        break;
                    case "lobby":

                        switch (command[1]){
                            case "players" -> {

                                switch (command[2]){

                                    case "initial" -> { // list of online players

                                        if (ViewManager.currentView instanceof Lobby lobby){

                                            String[] names = Arrays.copyOfRange(command, 3, command.length);

                                            for (String n : names){
                                                lobby.addPlayerToOnlineList(new Player(n));
                                            }
                                        }
                                    }
                                    case "add" -> {
                                        if (ViewManager.currentView instanceof Lobby lobby){
                                            lobby.addPlayerToOnlineList(new Player(command[3]));
                                        }
                                    }
                                    case "remove" -> {
                                        if (ViewManager.currentView instanceof Lobby lobby){
                                            lobby.removePlayerFromOnlineList(command[3]);
                                        }
                                    }
                                }


                            }
                        }

                        break;
                    case "game":
                        if(ViewManager.currentView == ViewManager.views.get(2)){    // current view needs to be the 'game view'

                            GameImproved game = (GameImproved) ViewManager.currentView;

                            switch (command[1]) {

                                case "chat" -> {

                                    sb.setLength(0);

                                    String[] message = Arrays.copyOfRange(command, 3, command.length);
                                    for (String x: message){sb.append(x).append(" ");}

                                    sb.setLength(sb.length()-1);

                                    SwingUtilities.invokeLater(() -> {
                                        game.appendToChat(command[2],sb.toString());
                                        game.chatPanel.repaint();
                                    });
                                                                    }

                                case "started" -> {

                                    game.pokerTable.gameStarted = true;
                                    GameImproved.bet = Float.parseFloat(command[2]);
                                    SwingUtilities.invokeLater(() -> {
                                        game.currentBet.setText("Current bet: "+Float.parseFloat(command[2]));
                                        game.appendToChat("server", "Game has started");
                                    });

                                }

                                case "currentBet" -> {
                                    SwingUtilities.invokeLater(() -> {game.currentBet.setText("Current bet: "+Float.parseFloat(command[2]));});
                                    GameImproved.bet = Float.parseFloat(command[2]);
                                }

                                case "action" -> {

                                    String[] actions = Arrays.copyOfRange(command, 2, command.length);

                                    for (String a : actions) {
                                        switch (a) {
                                            case "check" -> SwingUtilities.invokeLater(() -> game.actionsPanel.buttons.get(0).setEnabled(true));
                                            case "call" ->  SwingUtilities.invokeLater(() -> game.actionsPanel.buttons.get(1).setEnabled(true));
                                            case "bet" ->  SwingUtilities.invokeLater(() -> game.actionsPanel.buttons.get(2).setEnabled(true));
                                            case "raise" ->  SwingUtilities.invokeLater(() -> game.actionsPanel.buttons.get(3).setEnabled(true));
                                            case "fold" ->  SwingUtilities.invokeLater(() -> game.actionsPanel.buttons.get(4).setEnabled(true));

                                        }
                                    }
                                    game.actionsPanel.repaint();
                                }

                                case "new_round" -> {
                                    for (Player p: game.pokerTable.gamePlayers){
                                        p.bet = 0; }
                                    SwingUtilities.invokeLater(() ->game.pokerTable.repaint());
                                }

                                case "turn" -> {
                                    String player_turn = command[2];

                                    for (Player p: game.pokerTable.tablePlayers){
                                        p.hasTurn = p.name.equals(player_turn);
                                    }
                                    SwingUtilities.invokeLater(() ->game.pokerTable.repaint());
                                }

                                case "smallBlind" ->{
                                    String name = command[2];
                                    game.appendToChat("server", "small blind: "+name);
                                    game.getPlayer(name).bet = GameImproved.bet /2;
                                    SwingUtilities.invokeLater(() ->game.pokerTable.repaint());
                                }
                                case "bigBlind" ->{
                                    String name = command[2];
                                    game.appendToChat("server", "big blind: "+name);
                                    game.getPlayer(name).bet = GameImproved.bet;
                                    SwingUtilities.invokeLater(() ->game.pokerTable.repaint());
                                }
                                case "potMoney" ->{
                                    game.pokerTable.potMoney = Float.parseFloat(command[2]);
                                    SwingUtilities.invokeLater(() ->game.pokerTable.repaint());
                                }

                                case "players" ->{

                                    String[] playerList = Arrays.copyOfRange(command, 2, command.length);

                                    game.pokerTable.gamePlayers.clear();

                                    for (String x:playerList){
                                        game.pokerTable.gamePlayers.add(game.getPlayer(x));
                                        game.getPlayer(x).cards.add(new Card("hidden"));
                                        game.getPlayer(x).cards.add(new Card("hidden"));

                                    }

                                    game.pokerTable.tablePlayers = new ArrayList<>(game.pokerTable.gamePlayers);
                                    SwingUtilities.invokeLater(() ->game.pokerTable.repaint());

                                }

                                case "showdown" -> {for (Player p: game.pokerTable.gamePlayers) {p.cards.clear();}}
                                case "card" -> {
                                    if (player.cards.size()==2){player.cards.clear();}
                                    player.cards.add(new Card(command[2], command[3]));
                                    if (player.cards.size()==2){
                                        game.cardsPanel.cards[0] = player.cards.get(0);
                                        game.cardsPanel.cards[1] = player.cards.get(1);

                                        SwingUtilities.invokeLater(() ->game.cardsPanel.repaint());
                                        SwingUtilities.invokeLater(() ->game.pokerTable.repaint());

                                    }
                                }

                                case "reveal_card" -> {
                                    Player p = game.getPlayer(command[2]);
                                    p.cards.add(new Card(command[3], command[4]));
                                    SwingUtilities.invokeLater(() ->game.pokerTable.repaint());
                                }

                                case "player_action" -> {

                                    switch (command[2]){

                                        case "call" -> { // name
                                            game.appendToChat("server", command[3] + " calls");

                                           }
                                        case "raise" -> { // name, currentBet
                                            game.appendToChat("server", command[3] + " raises to "+command[4]+"0");
                                            GameImproved.bet = Float.parseFloat(command[4]);
                                            game.getPlayer(command[3]).bet = GameImproved.bet;
                                            SwingUtilities.invokeLater(() -> {
                                                game.currentBet.setText("Current bet "+GameImproved.bet);
                                                game.infoPanel.repaint();
                                            });


                                        }
                                        case "fold" -> { // name
                                            game.appendToChat("server", command[3] + " folds");

                                        }

                                        case "check" -> {
                                            game.appendToChat("server", command[3] + " checks");

                                        }

                                        case "bet" -> {
                                            game.appendToChat("server", command[3] + " bets "+ command[4]);
                                            game.getPlayer(command[3]).bet = Float.parseFloat(command[4]);
                                            GameImproved.bet = Float.parseFloat(command[4]);
                                            SwingUtilities.invokeLater(() -> {
                                                game.currentBet.setText("Current bet "+Float.parseFloat(command[4]));
                                                game.repaint();
                                            });

                                        }
                                    }

                                }

                                case "data" -> {

                                    switch (command[2]){
                                        case "players" -> {
                                            String[] playerList = Arrays.copyOfRange(command, 3, command.length);

                                            for (String p:playerList){
                                                if(p.equals(player.name)){
                                                    game.pokerTable.tablePlayers.add(player);
                                                }
                                                else {
                                                    game.pokerTable.tablePlayers.add(new Player(p));
                                                }
                                                SwingUtilities.invokeLater(() -> game.pokerTable.repaint());

                                            }
                                        }
                                        case "ingame" -> {

                                            String[] playerList = Arrays.copyOfRange(command, 3, command.length);
                                            for (String p:playerList){
                                                game.pokerTable.gamePlayers.add(game.getPlayer(p));
                                            }

                                            if (game.pokerTable.gamePlayers.size()>0){
                                                game.pokerTable.gameStarted = true;
                                            }
                                            for(Player p : game.pokerTable.gamePlayers){
                                                if (p.cards.size()==0){
                                                    p.cards.add(new Card("hidden"));
                                                    p.cards.add(new Card("hidden"));
                                                }
                                            }

                                        }
                                        case "update" -> {
                                            if (command[3].equals("add")){
                                                game.pokerTable.tablePlayers.add(new Player(command[4]));
                                                game.pokerTable.repaint();

                                            }
                                            else if (command[3].equals("remove")){

                                                game.pokerTable.removePlayer(command[4]);
                                                SwingUtilities.invokeLater(() -> game.pokerTable.repaint());
                                            }
                                        }
                                    }
                                }

                                case "winner" -> game.gameEnded(command[2]);
                                case "winners" -> {

                                    String[] args = Arrays.copyOfRange(command, 2, command.length);
                                    String[] playerList = Arrays.copyOf(args, args.length-1);
                                    game.gameEnded(playerList, Float.parseFloat(args[args.length-1]));

                                }

                                case "communityCard" -> {
                                    Card c = new Card(command[2], command[3]);
                                    game.pokerTable.communityCards.add(c);
                                    SwingUtilities.invokeLater(() -> game.pokerTable.repaint());


                                }
                                case "end" -> game.gameEnded("");

                            }

                        }
                }}

        }catch (Exception e){
            //e.printStackTrace();
        }

    }

    @Override
    public void close() throws Exception {
        writer.close();
        reader.close();
        socket.close();
        gameOver = true;
    }
}
