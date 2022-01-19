import java.util.*;

public class testClass {

    static List<Card> deck;


    static void gameLoop(){

        while (true){

        deck = Cards.generateDeck();
        Cards.shuffleDeck(deck);

        Player playerOne = new Player("Alex", 1000f, null);
        Player playerTwo = new Player("Emilia", 1000f, null);
        Player playerThree = new Player("Alice", 1000f, null);

        List<Player> players = new ArrayList<>();
        List<Card> tableCards = new ArrayList<>();


        players.add(playerOne);
        players.add(playerTwo);
        players.add(playerThree);

        for (Player p:players){
            p.addCard(takeCardFromDeck());
            p.addCard(takeCardFromDeck());
            System.out.println(p.name + " cards: " + p.getCards().toString());
        }

        for (int i=0;i<5;i++){
            tableCards.add(takeCardFromDeck());
        }

        System.out.println("\ncommunity cards :" + tableCards +"\n");

        List<Card> cards = new ArrayList<>();


        for (Player p : players){
            // cards should have a size of 7; 5 community cards and 2 player cards
            cards.addAll(tableCards);
            cards.addAll(p.getCards());
            cards = Cards.sortCards(cards);
            p.setAllCards(cards);
            Cards.setHand(p);   // sets player's hand (name and hands variables)
            cards.clear();
            System.out.println(p.name + ": " + p.hand + "  "+ p.getHandCards());
        }
        System.out.println("\n");
        System.out.println("Winner(s): "+Cards.getPlayerWithBestHand(players) +"\n_____________________\n\n");


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {

        gameLoop();

        /*deck = Cards.generateDeck();
        Cards.shuffleDeck(deck);
        Cards.shuffleDeck(deck);

        Player playerOne = new Player("Alex", 1000f, null);
        Player playerTwo = new Player("Emilia", 1000f, null);
        Player playerThree = new Player("Alice", 1000f, null);


        List<Player> players = new ArrayList<>();
        List<Card> tableCards = new ArrayList<>();


        // add players to table
        players.add(playerOne);
        players.add(playerTwo);
        players.add(playerThree);


        playerOne.addCard(new Card("5", "Spades"));
        playerOne.addCard(new Card("3", "Spades"));

        playerTwo.addCard(new Card("7", "Spades"));
        playerTwo.addCard(new Card("5", "Clubs"));

        playerThree.addCard(new Card("10", "Spades"));
        playerThree.addCard(new Card("4", "Clubs"));



        // give players cards
        for (Player player : players){
           // player.addCard(takeCardFromDeck());
            //player.addCard(takeCardFromDeck());

            System.out.println(player.name + " cards: " + player.getCards().toString());
        }

        // add cards to the table
        tableCards.add(new Card("Q", "Spades"));
        tableCards.add(new Card("J", "Clubs"));
        tableCards.add(new Card("8", "Spades"));
        tableCards.add(new Card("2", "Clubs"));
        tableCards.add(new Card("A", "Hearts"));




        *//*

        tableCards.add(takeCardFromDeck());
        tableCards.add(takeCardFromDeck());
        tableCards.add(takeCardFromDeck());
        tableCards.add(takeCardFromDeck());
        tableCards.add(takeCardFromDeck());
*//*

        System.out.println("\ncommunity cards :" + tableCards +"\n");


        // set players 'hands'
        List<Card> cards = new ArrayList<>();
        for (Player p : players){

            // cards should have a size of 7; 5 community cards and 2 player cards
            cards.addAll(tableCards);
            cards.addAll(p.getCards());

            cards = Cards.sortCards(cards);
            p.setAllCards(cards);
            Cards.setHand(p);   // sets player's hand (name and hands variables)

            cards.clear();


            System.out.println(p.name + ": " + p.hand + "  "+ p.getHandCards());

        }
        System.out.println("\n");
        System.out.println("Winner(s): "+Cards.getPlayerWithBestHand(players));*/
    }
    private static Card takeCardFromDeck() {
        if(!deck.isEmpty()){
            Card card = deck.get(0);
            deck.remove(0);
            return card;
        }
        return null;
    }



}
