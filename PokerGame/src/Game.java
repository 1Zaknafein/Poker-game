import jdk.swing.interop.SwingInterOpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game {

    boolean ended = false;
    List<Player> players;
    private float currentBet, smallBlind, bigBlind;
    private Player smallBlindPlayer, bigBlindPlayer;
    private float potMoney;
    final Table gameTable;
    List<Card> deck;
    List<Player> playersFolded;
    List<Player> playersLeftTable;

    Player playerTurn;

    private List<Card> communityCards;

    public Game(List<Player> players, Table table){
        this.players = new ArrayList<>(players);
        gameTable = table;
    }

    public void startGame(){

        try {
            if (ended ){return;}

            potMoney = 0;
            currentBet = gameTable.getBet();
            sendToAllInGame("game started "+currentBet);
            //print("\n\n____________\n\nGame on table "+gameTable.getId() + " started");
            print("Game started \n__________\n");
            deck = Cards.generateDeck();
            Cards.shuffleDeck(deck);
            communityCards = new ArrayList<>();
            playersLeftTable = new ArrayList<>();

            for (Player p: players){
                p.resetData();
                p.game = this;
            }

            smallBlind = currentBet/2;
            bigBlind = currentBet;

            smallBlindPlayer = players.get(0);
            bigBlindPlayer = players.get(1);
            betAmount(smallBlindPlayer, smallBlind);
            betAmount(bigBlindPlayer, bigBlind);

            assignCardsToPlayers();
            sendPlayerPositions();
            informAboutCardsAndBlinds();

            for (Player p: players){ p.handler.sendToPlayer("player money "+p.getMoney()); }
            firstRound();

        }

        catch (IndexOutOfBoundsException e){
            //restartGame();
            //Thread.currentThread().interrupt();
            //Thread.currentThread().stop();
            e.printStackTrace();

        }

    }

    public void restartGame(){
        while (true){

            synchronized (gameTable){

                        if (gameTable.getPlayers().size() > 1) {

                            // changing everyones position so that big/small blind is assigned to different player
                            Player temp = gameTable.getPlayers().get(gameTable.getPlayers().size()-1);
                            for (int i = gameTable.getPlayers().size() - 1; i > 0; i--) {
                                gameTable.getPlayers().set(i, gameTable.getPlayers().get(i - 1));
                            }
                            gameTable.getPlayers().set(0, temp);

                            players = new ArrayList<>(gameTable.getPlayers());
                            ended = false;
                            break;
                        }
                    }
            try{
                Thread.sleep(2000);
            } catch (InterruptedException ee){ee.printStackTrace();}
        }
        startGame();
    }

    public float getCurrentBet() {
        return currentBet;
    }
    public float getSmallBlind() {
        return smallBlind;
    }
    public float getBigBlind() {
        return bigBlind;
    }
    public List<Player> getPlayers() {
        return players;
    }
    public float getPotMoney(){return potMoney;}


    private Card takeCardFromDeck() {
        if(!deck.isEmpty()){
            Card card = deck.get(0);
            deck.remove(0);
            return card;
        }
        return null;
    }

    private void assignCardsToPlayers(){
        for (Player player : players){              // assign 2 cards to each player in game
            player.addCard(takeCardFromDeck());
            player.addCard(takeCardFromDeck());
            print(player.name + "'s cards: " + player.getCards().toString());
        }
    }

    private void sendPlayerPositions(){
        StringBuilder sb = new StringBuilder();
        sb.append("game players");

        // need to send data to all players in the table, rather to those in the game only
        for (Player p : gameTable.getPlayers()){
            sb.append(" ").append(p.name);
        }

        // game positions name1 name2 name3
        sendToAllInTable(sb.toString());
    }

    private void informAboutCardsAndBlinds(){

        for (Player p: gameTable.getPlayers()){
            p.handler.sendToPlayer("game smallBlind "+players.get(0).name);
            p.handler.sendToPlayer("game bigBlind "+players.get(1).name);

            if (p.getCards().size()==2){
            for (Card c : p.getCards()){
                p.handler.sendToPlayer("game card "+c.name + " "+c.suit);
            }}
        }
    }

    private void firstRound(){
        print("first round started");
        playersFolded = new ArrayList<>();

        if(players.size()>2){
            for (int i=2; i<players.size();i++){

                Player p = players.get(i);
                playerTurn = p;
                sendToAllInTable("game turn " + p.name);
                p.handler.sendToPlayer("game action call raise fold");
                p.action.set(0);
                handleAction(p);
            }
        }

        removePlayersWhoFolded();

        if(players.size()==1){
            playerWon(players.get(0));return;
        }
        try{
            // small blind's turn
            playerTurn = smallBlindPlayer;
            sendToAllInTable("game turn " + smallBlindPlayer.name);
            smallBlindPlayer.handler.sendToPlayer("game action call raise fold");
            handleAction(smallBlindPlayer);

            removePlayersWhoFolded();

            // all other players left, so the winner is picked
            if(players.size()==1){
                playerWon(players.get(0));return;
            }

            // big blind's turn
            playerTurn = bigBlindPlayer;
            sendToAllInTable("game turn " + bigBlindPlayer.name);
            if(bigBlindPlayer.bet==currentBet){
                bigBlindPlayer.handler.sendToPlayer("game action check raise fold");
            }else {
                bigBlindPlayer.handler.sendToPlayer("game action call raise fold");
            }
            handleAction(bigBlindPlayer);


            // remove players who folded from the game (but still can receive game updates)
            removePlayersWhoFolded();


        }catch (IndexOutOfBoundsException e){
            if(players.size()==1){
                playerWon(players.get(0));return;

            }
            if (players.size()==0){
                // nobody in the game, end
                print("everyone left the game");
                gameEnded();
                return;

            }
        }
        //need to check that all bets are equal
        // size of the list 'players' is at least 2, otherwise game would end before this point
        while(!betsAreEqual()){
            if(players.size()>2){
                for (int i=2; i<players.size(); i++){
                    Player p = players.get(i);
                    if (p.bet != currentBet){
                        playerTurn = p;
                        sendToAllInTable("game turn " + p.name);
                        p.handler.sendToPlayer("game action call raise fold");
                        handleAction(p);
                    }
                }
            }

            removePlayersWhoFolded();

            if(players.size()==1){
                playerWon(players.get(0));return;

            }

            if(players.get(0).bet != currentBet){
                playerTurn = players.get(0);
                sendToAllInTable("game turn " + players.get(0).name);
                players.get(0).handler.sendToPlayer("game action call raise fold");
                handleAction(players.get(0));
            }
            removePlayersWhoFolded();
            if(players.size()==1){
                playerWon(players.get(0));return;

            }


            if (players.get(1).bet != currentBet){
                playerTurn = players.get(1);
                sendToAllInTable("game turn " + players.get(1).name);
                players.get(1).handler.sendToPlayer("game action call raise fold");
                handleAction(players.get(1));
            }
        }

        players.retainAll(gameTable.getPlayers());

        if(players.size()==1){
            playerWon(players.get(0));return;
        }

        sendToAllInTable("game potMoney "+potMoney);
        currentBet = 0;
        for (Player p: players){
            p.bet=0;
        }
        sendToAllInTable("game currentBet "+currentBet);
        if (players.size()==0){gameEnded(); return;}
        secondRound();

    }

    private void secondRound(){

        synchronized (gameTable){for (Player p:gameTable.getPlayers()){p.action.set(0);}}

        print("round two started with "+players.toString());
        sendToAllInTable("game new_round");

        // add 3 community cards
        communityCards.add(takeCardFromDeck());
        communityCards.add(takeCardFromDeck());
        communityCards.add(takeCardFromDeck());

        // inform players about added community cards (visible to all)
        sendToAllInTable("game communityCard "+communityCards.get(0).name + " "+communityCards.get(0).suit);
        sendToAllInTable("game communityCard "+communityCards.get(1).name + " "+communityCards.get(1).suit);
        sendToAllInTable("game communityCard "+communityCards.get(2).name + " "+communityCards.get(2).suit);

        // small blind player (or id 0, in case he did not make it to round two)
        playerTurn = players.get(0);
        sendToAllInTable("game turn " + players.get(0).name);
        players.get(0).handler.sendToPlayer("game action check bet fold");
        handleAction(players.get(0));

        removePlayersWhoFolded();
        if(players.size()==1){playerWon(players.get(0));}

        StringBuilder sb = new StringBuilder();
        for (int i=1; i<players.size();i++){
            Player p = players.get(i);
            p.action.set(0);
            playerTurn = p;
            sendToAllInTable("game turn " + p.name);
            sb.append("game action raise fold");
            if(p.bet==currentBet){
                sb.append(" check");
            }else {
                sb.append(" call");
            }
            p.handler.sendToPlayer(sb.toString());
            sb.setLength(0);
            handleAction(p);
        }

        removePlayersWhoFolded();
        if(players.size()==1){
            playerWon(players.get(0));
            return;
        }
        if (players.size()==0){gameEnded(); return;}


        while(!betsAreEqual()){

            if(players.get(0).bet != currentBet){
                playerTurn = players.get(0);
                sendToAllInTable("game turn " + players.get(0).name);
                players.get(0).handler.sendToPlayer("game action call raise fold");
                handleAction(players.get(0));
            }


            if(players.size()>1){
                for (int i=1; i<players.size(); i++){
                    Player p = players.get(i);
                    if (p.bet != currentBet){
                        p.action.set(0);
                        playerTurn = p;
                        sendToAllInTable("game turn " + p.name);
                        p.handler.sendToPlayer("game action call raise fold");
                        handleAction(p);
                    }
                }
            }
            removePlayersWhoFolded();
            if(players.size()==1){ playerWon(players.get(0)); return;}
            if (players.size()==0){gameEnded(); return;}

        }


        players.retainAll(gameTable.getPlayers());

        if(players.size()==1){
            playerWon(players.get(0));return;
        }

        sendToAllInTable("game potMoney "+potMoney);
        currentBet = 0;
        for (Player p: players){
            p.bet=0;
        }
        sendToAllInTable("game currentBet "+currentBet);
        if (players.size()==0){gameEnded(); return;}

        thirdRound();
    }


    private void thirdRound(){
        synchronized (gameTable){for (Player p:gameTable.getPlayers()){p.action.set(0);}}

        print("round three started with "+players.toString());
        sendToAllInTable("game new_round");
        // add 1 community card
        communityCards.add(takeCardFromDeck());

        // inform players about added community card (visible to all)
        sendToAllInTable("game communityCard "+communityCards.get(3).name + " "+communityCards.get(3).suit);


        // small blind player (or id 0, in case he did not make it to round two)
        playerTurn = players.get(0);
        sendToAllInTable("game turn " + players.get(0).name);
        players.get(0).handler.sendToPlayer("game action check bet fold");
        handleAction(players.get(0));

        removePlayersWhoFolded();
        if(players.size()==1){playerWon(players.get(0));}

        StringBuilder sb = new StringBuilder();
        for (int i=1; i<players.size();i++){
            Player p = players.get(i);
            p.action.set(0);
            playerTurn = p;
            sendToAllInTable("game turn " + p.name);
            sb.append("game action raise fold");
            if(p.bet==currentBet){
                sb.append(" check");
            }else {
                sb.append(" call");
            }
            p.handler.sendToPlayer(sb.toString());
            sb.setLength(0);
            handleAction(p);
        }

        removePlayersWhoFolded();
        if(players.size()==1){
            playerWon(players.get(0));
            return;
        }
        if (players.size()==0){gameEnded(); return;}


        while(!betsAreEqual()){

            if(players.get(0).bet != currentBet){
                playerTurn = players.get(0);
                sendToAllInTable("game turn " + players.get(0).name);
                players.get(0).handler.sendToPlayer("game action call raise fold");
                handleAction(players.get(0));
            }


            if(players.size()>1){
                for (int i=1; i<players.size(); i++){
                    Player p = players.get(i);
                    if (p.bet != currentBet){
                        p.action.set(0);
                        playerTurn = p;
                        sendToAllInTable("game turn " + p.name);
                        p.handler.sendToPlayer("game action call raise fold");
                        handleAction(p);
                    }
                }
            }
            removePlayersWhoFolded();
            if(players.size()==1){ playerWon(players.get(0)); return;}
            if (players.size()==0){gameEnded(); return;}

        }

        players.retainAll(gameTable.getPlayers());

        if(players.size()==1){
            playerWon(players.get(0));return;
        }

        sendToAllInTable("game potMoney "+potMoney);
        currentBet = 0;
        for (Player p: players){
            p.bet=0;
        }
        sendToAllInTable("game currentBet "+currentBet);
        if (players.size()==0){gameEnded(); return;}

        fourthRound();

    }

    private void fourthRound(){
        synchronized (gameTable){for (Player p:gameTable.getPlayers()){p.action.set(0);}}

        print("round four started with "+players.toString());
        sendToAllInTable("game new_round");
        // add 1 community card
        communityCards.add(takeCardFromDeck());



        // inform players about added community card (visible to all)
        sendToAllInTable("game communityCard "+communityCards.get(4).name + " "+communityCards.get(4).suit);


        // small blind player (or id 0, in case he did not make it to round two)
        playerTurn = players.get(0);
        sendToAllInTable("game turn " + players.get(0).name);
        players.get(0).handler.sendToPlayer("game action check bet fold");
        handleAction(players.get(0));

        removePlayersWhoFolded();
        if(players.size()==1){playerWon(players.get(0));}

        StringBuilder sb = new StringBuilder();
        for (int i=1; i<players.size();i++){
            Player p = players.get(i);
            p.action.set(0);
            playerTurn = p;
            sendToAllInTable("game turn " + p.name);
            sb.append("game action raise fold");
            if(p.bet==currentBet){
                sb.append(" check");
            }else {
                sb.append(" call");
            }
            p.handler.sendToPlayer(sb.toString());
            sb.setLength(0);
            handleAction(p);
        }

        removePlayersWhoFolded();
        if(players.size()==1){
            playerWon(players.get(0));
            return;
        }
        if (players.size()==0){gameEnded(); return;}


        while(!betsAreEqual()){

            if(players.get(0).bet != currentBet){
                playerTurn = players.get(0);
                sendToAllInTable("game turn " + players.get(0).name);
                players.get(0).handler.sendToPlayer("game action call raise fold");
                handleAction(players.get(0));
            }


            if(players.size()>1){
                for (int i=1; i<players.size(); i++){
                    Player p = players.get(i);
                    if (p.bet != currentBet){
                        p.action.set(0);
                        playerTurn = p;
                        sendToAllInTable("game turn " + p.name);
                        p.handler.sendToPlayer("game action call raise fold");
                        handleAction(p);
                    }
                }
            }
            removePlayersWhoFolded();
            if(players.size()==1){ playerWon(players.get(0)); return;}
            if (players.size()==0){gameEnded(); return;}

        }

        // remaining players show their cards
        players.retainAll(gameTable.getPlayers());

        if(players.size()==1){
            playerWon(players.get(0));return;
        }

        sendToAllInTable("game potMoney "+potMoney);
        currentBet = 0;
        for (Player p: players){
            p.bet=0;
        }
        sendToAllInTable("game currentBet "+currentBet);
        if (players.size()==0){gameEnded(); return;}

        sendToAllInTable("game showdown");

        for (Player p: players){
            for (Card c: p.getCards()){
                sendToAllInTable("game reveal_card "+p.name+ " " + c.name + " " + c.suit);
            }
        }
        try {
            Thread.sleep(1000);
        }catch (InterruptedException ignored){}

        // select best hand and pick winner
        List<Player> winners;
        int i=0;
        while(true){
            List<Card> cards = new ArrayList<>();
            for (Player p: players){
                cards.addAll(communityCards);
                cards.addAll(p.getCards());

                cards = Cards.sortCards(cards);
                p.setAllCards(cards);
                Cards.setHand(p);

                cards.clear();

                print(p.name + ": " + p.hand + "  "+ p.getHandCards());
            }

            winners = new ArrayList<>(Cards.getPlayerWithBestHand(players));

            if (i==3){
                print("something went wrong in selecting a winner");
                gameEnded();
            }
            i++;
            if (!winners.isEmpty()){
                break;
            }
        }


        print("Winner(s): "+winners.toString());

        if (winners.size()==1){playerWon(winners.get(0));}
        else if (winners.size()== 0){
            //print("no winner?");
            print("there was no winner... somehow");
            gameEnded();
        }
        else {
            playerWon(winners);
        }

    }


    private boolean canBet(Player p){
        return p.getMoney()>currentBet;
    }

    private void betAmount(Player p, float amount){
        if (p.getMoney() >= currentBet){
            p.bet += amount;
            potMoney += amount;
            p.giveMoney(-amount);
            sendToAllInTable("game player_action bet "+p.name + " " + p.bet);

            print("added to pot "+amount);
        }
        else {
            print("player "+p.name+ " has insufficient funds!");
        }

    }

    private void handleAction(Player p) {

        // action 1: call
        // action 2: raise
        // action 3: fold
        // action 4: check
        // action 5: bet

       /* while (p.action.get() == 0) {
            try {
                if (p.game == null) {
                    print(p.name + " left the game");
                    p.action.set(3);
                    break;
                }
                if (p.table!=gameTable){
                    p.action.set(3);
                    break;
                }
                if (players.size() == 1) {
                    players.get(0).action.set(0);
                    playerWon(players.get(0));
                    return;
                }
                print("waiting for player " + p.name + " to make an action...");
                synchronized (this) {
                    if (playerTurn==p){
                        this.wait();
                    }else {return;}
                    playerTurn=null;
                }       // now doesn't need to check every 2 seconds, its notified whenever player performs one of the actions
                // and resumed then

                //Thread.sleep(2000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                p.action.set(3);
            }
        }*/

        while(p.action.get() == 0){

            try {
                // if player's game is null - usually when left game while having turn
                if (p.game == null) {
                    print(p.name + " left the game");
                    p.action.set(3);
                    //break;
                }
                // if somehow player's table is not the same as game table
                if (p.table!=gameTable){
                    p.action.set(3);
                    //break;
                }

                // check if last player in the game
                if (players.size() == 1) {
                    players.get(0).action.set(0);
                    playerWon(players.get(0));
                    //return;
                }

                // important check, because players in the 'players' list are removed after round loop, to avoid concurrent modification exception
                int i =0;
                for(Player x: players){
                    if (!playersFolded.contains(x)){
                        i+=1;
                    }
                }
                if (i==1){playerWon(players.get(0));}

                //print("waiting for player " + p.name + " to make an action...");
                Thread.sleep(2000);


            } catch (InterruptedException e) {
                e.printStackTrace();
                p.action.set(3);
            }

        }


        if (p.action.get() == 1) {
            // puts in current bet / calls
            betAmount(p, currentBet-p.bet);
            sendToAllInTable("game player_action call "+p.name);
            print(p.name + " calls!");


        } else if (p.action.get() == 2) {
            // raises
            if (p.raiseTo>currentBet){
                currentBet = p.raiseTo;
                betAmount(p, (currentBet - p.bet));
                sendToAllInTable("game player_action raise "+p.name + " "+currentBet);
                print(p.name + " raises the bet to " + currentBet);

            }

        } else if (p.action.get() == 3) {
            // folds - remove from the game
            playersFolded.add(p); // will remove players in this list from the list of players in game
            sendToAllInTable("game player_action fold "+p.name);
            print(p.name + " folds!");
        }
        else if(p.action.get() == 4){
            sendToAllInTable("game player_action check "+p.name);
            print(p.name + " checks!");
        }

        else if(p.action.get() == 5){
            currentBet = p.raiseTo;
            betAmount(p, currentBet);
            //sendToAllInTable("game player_action bet "+p.name + " "+currentBet);
            print(p.name + " bets " + currentBet);
        }
        p.action.set(0); // reset action for next turn
    }

    void playerWon(Player player){
        sendToAllInTable("game winner "+player.name);
        player.giveMoney(potMoney);
        print(player.name + " has won "+potMoney);

        for (Player p:gameTable.getPlayers()){
            p.action.set(0);
        }
        gameEnded();
    }

    void playerWon(List<Player> winners){
        StringBuilder sb = new StringBuilder();
        sb.append("game winners");

        for (Player p: winners){sb.append(" ").append(p.name);}

        int number_of_winners = winners.size();
        float prize = potMoney / number_of_winners;

        sb.append(" ").append(prize);
        sendToAllInTable(sb.toString());

        for (Player p: winners){
            p.giveMoney(prize);
            print(p.name + " has won "+prize);
        }

        for (Player p:gameTable.getPlayers()){
            p.action.set(0);
        }
        gameEnded();
    }

    void gameEnded(){
        sendToAllInTable("game end");
        print("game ended");
        players.clear();
        ended=true;
        restartGame();
        gameTable.game=null;
    }

    private void sendToAllInTable(String cmd){
        synchronized (gameTable){
            for (Player p: gameTable.getPlayers()){
                try{
                    p.handler.sendToPlayer(cmd);
                }catch (Exception x){x.printStackTrace(); }

            }
        }

    }
    private void sendToAllInGame(String cmd){
        for (Player p: players){
            p.handler.sendToPlayer(cmd);
        }
    }
    private boolean betsAreEqual(){
        for (Player p : players){
            if (p.bet != currentBet){
                return false;
            }
        }
        return true;
    }
    private void removePlayersWhoFolded(){
        players.removeAll(playersFolded);
        playersFolded.clear();
    }

    private void print(String text){
        System.out.println("table " + gameTable.getId() + ": "+text);
    }
}
