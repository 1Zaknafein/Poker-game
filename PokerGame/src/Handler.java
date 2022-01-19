import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Handler implements Runnable {

    private final Socket socket;
    private Scanner reader;     // to get data from players
    private PrintWriter writer; // to send data to players
    private final ReentrantLock lock = new ReentrantLock();
    private boolean isPlayerConnected;
    Player player;
    StringBuilder sb = new StringBuilder();

    public Handler(Socket socket){
        this.socket = socket;
        isPlayerConnected = false;
    }

    private void setupStreams(){
        try{
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new Scanner(socket.getInputStream());
        }
        catch (IOException x){
            System.out.println("setting up streams failed");
        }
    }

    private void newPlayerConnected(){
        isPlayerConnected = true;
    }

    @Override
    public void run() {

        setupStreams();
        newPlayerConnected();
        try {
            String line;
            String[] data;
            while(isPlayerConnected) {

                //get input from player
                line = reader.nextLine();
                if (line.length()>0){
                    data = line.split(" ");
                    handleCommand(data);
                }
        }
        }catch (Exception e){
            closeConnection();
        }
    }

    private void handleCommand(String[] command){

        //System.out.println("client -> server "+ Arrays.toString(command));
        try{
            switch (command[0]) {

                case "player" -> {
                    String arg = command[1];

                    switch (arg){

                        case "name" -> {
                            String name = command[2];

                            // check if player with that name exists
                            boolean exists = false;
                            for (Player p : ServerMain.onlinePlayers){
                                if (p.name.equalsIgnoreCase(name)){
                                    sendToPlayer("player exists");   // dont send data if so
                                    exists=true;
                                    closeConnection();
                                    break;
                                }
                            }
                            // else send data
                            if(!exists){
                                sendToPlayer("player allowed");
                                player = new Player(name, 10000, this);
                                lock.lock();
                                ServerMain.onlinePlayers.add(player);
                                lock.unlock();
                                //System.out.println("Player " + player.name + " (" + socket.getInetAddress().getHostAddress() + ") connected.");
                                System.out.println("Player " + player.name + " connected.");

                                sendToPlayer("player money "+player.getMoney());

                                sendTableMainInfo();

                                // send update for all other players
                                synchronized (ServerMain.onlinePlayers){
                                    for (Player p: ServerMain.onlinePlayers){
                                        if (p!=player){
                                            p.handler.sendToPlayer("lobby players add "+player.name);
                                        }
                                    }
                                }
                            }

                        }

                        case "disconnected" -> {
                            closeConnection();
                        }
                    }


                }
                case "joinTable" -> {
                    int table_id = Integer.parseInt(command[1]);

                    lock.lock();
                    for (Table t : ServerMain.tables) {

                        if (table_id == t.getId()) {
                            t.addPlayer(player);
                        }
                    }
                    lock.unlock();
                }
                case "lobby" -> {
                    switch (command[1]){
                        // send latest data on tables (for lobby view)
                        case "update" -> sendTableMainInfo();
                    }
                }
                case "game" ->{

                    String arg = command[1];

                    switch (arg) {

                        case "chat":

                            if (player.table!=null && !command[2].strip().equals("")){

                                sb.setLength(0);



                                String[] message = Arrays.copyOfRange(command, 2, command.length);


                                for (String x:message){
                                    sb.append(" ");
                                    sb.append(x);
                                }

                                synchronized (player.table){

                                    for (Player p: player.table.getPlayers()){
                                        p.handler.sendToPlayer("game chat "+ player.name+sb.toString());
                                    }
                                }
                                sb.setLength(0);
                            }

                            break;
                        case "leave":
                            // remove player from the table (and game??)

                            synchronized (player) {
                                player.ready = false;
                                player.action.set(3);
                                player.table.removePlayer(player);
                            }

                            break;
                        case "action":

                            switch (command[2]) {
                                case "call" -> {
                                    player.action.set(1);
                                    //synchronized (player.game){player.game.notify();}
                                }
                                case "raise" -> {
                                    player.raiseTo = Float.parseFloat(command[3]);
                                    player.action.set(2);
                                    //synchronized (player.game){player.game.notify();}
                                }
                                case "fold" -> {
                                    player.action.set(3);
                                    //synchronized (player.game){player.game.notify();}
                                }
                                case "check" -> {
                                    player.action.set(4);
                                    //synchronized (player.game){player.game.notify();}
                                }
                                case "bet" -> {
                                    player.raiseTo = Float.parseFloat(command[3]);
                                    player.action.set(5);
                                    //synchronized (player.game){player.game.notify();}
                                }

                            }
                            break;
                    }

                }
                default -> throw new Exception("Unknown command '" + command[0] + "' from " + socket.getInetAddress().getHostAddress());
            }
        }catch (Exception x){
            closeConnection();
        }

    }

    public void sendToPlayer(String command){
        if (command.isEmpty()){
            System.out.println("Cannot send empty command!");
        }
        else if(command.split(" ").length<2){
            System.out.println("Command does not have arguments!");
        }else {
            lock.lock();
            writer.println(command);
            lock.unlock();
        }

    }


    public static void sendToAllPlayers(String command, boolean onlyPlayersInLobby){

        for (Player p : ServerMain.onlinePlayers){
            if(onlyPlayersInLobby){
                if(p.table == null){ // if not in a table, because no need to inform players about the change if they play
                    p.handler.sendToPlayer(command);
                }
            }else {
                // send to all players, including if in game
                // nothing so far
            }
        }
    }

    private void sendTableMainInfo(){   // send list of tables and players
        for (Table table : ServerMain.tables){
            // table -> id -> players in the table -> max players -> min bet
            int id = table.getId();
            int players  = table.getNumberOfPlayers();
            int maxPlayers = table.maxPlayers;
            float minBet = table.getBet();

            sendToPlayer("table initial "+ id + " " + players + " " + maxPlayers + " " + minBet );
        }

        StringBuilder sb = new StringBuilder();

        sb.append("lobby players initial");
        synchronized (ServerMain.onlinePlayers){
            for (Player p : ServerMain.onlinePlayers){
                sb.append(" ").append(p.name);
            }
        }
        sendToPlayer(sb.toString());        // table players <players>

    }

    private void closeConnection(){
        try {
            reader.close();
            writer.close();
            socket.close();

        }catch (IOException e){e.printStackTrace();}
        finally {
            if(player!=null){
                if(player.name!=null) {
                    if (player.table!=null){
                        if (player.game!=null){
                            synchronized (player.game){
                                player.action.set(3);
                                player.game.notify();
                                }}

                        sendToAllPlayers("table update "+ player.table.getId() + " " + (player.table.getNumberOfPlayers()-1), true);
                        player.table.removePlayer(player);
                        player.game=null;
                        player.table=null;

                    }
                    System.out.println("Player " + player.name + " disconnected.");

                    synchronized (ServerMain.onlinePlayers){
                        for (Player p: ServerMain.onlinePlayers){
                            if (p!=player){
                                p.handler.sendToPlayer("lobby players remove "+player.name);
                            }
                        }
                        ServerMain.onlinePlayers.remove(player);

                    }

                    player = null;
                }
            }

        }
        Thread.currentThread().interrupt();
    }
}
