import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {

    private List<Player> players;
    private float minimumBet;
    private List<Card> communityCards;
    private final int id;
    public final int maxPlayers;
    Game game;

    int gameStartDelay = 8000;

    public Table(float bet, int table_id, int maxPlayers){
        players = new ArrayList<>();
        minimumBet = bet;
        communityCards = new ArrayList<>();
        id = table_id;
        this.maxPlayers = maxPlayers;
        runGame(players, this);
    }

    public boolean isEmpty(){
        return players.isEmpty();
    }

    public synchronized int getId(){
        return id;
    }
    public void addPlayer(Player player){

        // check if table is not full and no same username already inside

        if(getNumberOfPlayers()==maxPlayers || players.contains(player)){
            player.handler.sendToPlayer("table failed");
        }else {
            System.out.println("Player "+player.name + " joined table "+id);
            players.add(player);
            player.table = this;
            Handler.sendToAllPlayers("table update "+ id + " " + getNumberOfPlayers(), true);// update players in lobby (+1 to number of players in this table)
            player.handler.sendToPlayer("table success");   // so that window can be switched to table/game view

            StringBuilder sb = new StringBuilder();
            for (Player p:players){
                sb.append(" ").append(p.name);
            }
            player.handler.sendToPlayer("game data players"+sb.toString());    // players currently in table
            sb.setLength(0);


            if(game!=null){
                List<Player> tempList;
                    tempList = new ArrayList<>(players);   // a list of players currently in the game (or 'active')
                    tempList.retainAll(game.players);
                    for (Player p:tempList){
                        sb.append(" ").append(p.name);
                    }
                    player.handler.sendToPlayer("game data ingame"+sb.toString());
                    sb.setLength(0);



            }
            // inform all other players in table, so they can update their lists
            for(Player p:players){
                if (p!=player){ p.handler.sendToPlayer("game data update add "+player.name); } }
        }

        //Collections.shuffle(players); // shuffling players so blinds are assigned randomly
    }

    public synchronized void removePlayer(Player p){

        if (p.game!=null){
            if (p.game.playerTurn == p){
                p.action.set(3);
                    //p.game.notifyAll();
                p.game.playersFolded.add(p);
            }

            p.game = null;
        }
        Handler.sendToAllPlayers("table update "+ id + " " + (getNumberOfPlayers()-1), true);
        System.out.println("Player "+p.name + " left table "+id);

        players.remove(p);
        p.table=null;

        for (Player x: players){
            x.handler.sendToPlayer("game data update remove "+p.name);
        }
    }


    public void increaseBet(float bet){
        if(bet>0){
            minimumBet+=bet;
        }
    }

    public List<Player> getPlayers(){
        return players;
    }
    public int getNumberOfPlayers(){return players.size();}
    public void addCard(Card card){
        communityCards.add(card);
    }
    public List<Card> getCards(){
        return communityCards;
    }

    public float getBet(){return minimumBet;}

    private synchronized boolean tableReady(){
        if(getNumberOfPlayers() == maxPlayers){ return true; }
        return getNumberOfPlayers() > 1;
    }

    public void runGame(List<Player> p, Table t){
        Thread listener = new Thread(){
            public void run(){
                try {

                    while(true){

                        if (tableReady()){
                            Thread.sleep(gameStartDelay);
                            if (tableReady()){
                                game = new Game(p, t);
                                game.startGame();
                                break;
                            }

                        }
                        else {
                            Thread.sleep(3000);
                        }
                    }
                }catch (Exception e){ e.printStackTrace(); }
            }
        };
        listener.start();
    }

}
