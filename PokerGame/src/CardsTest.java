import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class CardsTest {

    Player p1 = new Player("one",100,null);


    @org.junit.jupiter.api.Test
    void generateDeck() {

        List<Card> deck = Cards.generateDeck();
        int clubs = 0;
        int diamonds = 0;
        int hearts = 0;
        int spades = 0;
        for (Card c:deck) {
            switch (c.suit) {
                case "Diamonds" -> diamonds += 1;
                case "Clubs" -> clubs += 1;
                case "Hearts" -> hearts += 1;
                case "Spades" -> spades += 1;
            }

        }

        // should be 52 cards in whole card deck
        assertEquals(52, deck.size());

        // every card rank should have 4 different suits
        assertEquals((diamonds == clubs), (hearts == spades));
        assertEquals(52/4, clubs);
        assertEquals(52/4, diamonds);
    }

    @org.junit.jupiter.api.Test
    void setHand() {

        // given 7 cards, find the best 5-card hand combination and assign hand name

        List<Card> cards = new ArrayList<>();

        // pair
        cards.add(new Card("A","Diamonds"));
        cards.add(new Card("A","Clubs"));
        cards.add(new Card("K","Diamonds"));
        cards.add(new Card("5","Diamonds"));
        cards.add(new Card("2","Spades"));
        cards.add(new Card("1","Hearts"));
        cards.add(new Card("3","Clubs"));

        assertEquals("pair", getHand(cards));
        cards.clear();

        //two pair
        cards.add(new Card("A","Diamonds"));
        cards.add(new Card("A","Clubs"));
        cards.add(new Card("2","Diamonds"));
        cards.add(new Card("K","Spades"));
        cards.add(new Card("2","Spades"));
        cards.add(new Card("1","Hearts"));
        cards.add(new Card("3","Clubs"));

        assertEquals("two pair", getHand(cards));
        cards.clear();

        //royal flush
        cards.add(new Card("A","Diamonds"));
        cards.add(new Card("K","Diamonds"));
        cards.add(new Card("Q","Diamonds"));
        cards.add(new Card("10","Diamonds"));
        cards.add(new Card("J","Diamonds"));
        cards.add(new Card("J","Hearts"));
        cards.add(new Card("J","Clubs"));

        assertEquals("royal flush", getHand(cards));
        cards.clear();

        //straight
        cards.add(new Card("9","Diamonds"));
        cards.add(new Card("8","Clubs"));
        cards.add(new Card("7","Diamonds"));
        cards.add(new Card("10","Diamonds"));
        cards.add(new Card("6","Hearts"));
        cards.add(new Card("J","Hearts"));
        cards.add(new Card("J","Clubs"));

        assertEquals("straight", getHand(cards));
        cards.clear();

        //four of a kind
        cards.add(new Card("5","Diamonds"));
        cards.add(new Card("3","Clubs"));
        cards.add(new Card("5","Clubs"));
        cards.add(new Card("10","Diamonds"));
        cards.add(new Card("5","Hearts"));
        cards.add(new Card("J","Hearts"));
        cards.add(new Card("5","Spades"));

        assertEquals("four of a kind", getHand(cards));
        cards.clear();

        //three of a kind
        cards.add(new Card("5","Diamonds"));
        cards.add(new Card("5","Clubs"));
        cards.add(new Card("5","Spades"));
        cards.add(new Card("10","Diamonds"));
        cards.add(new Card("2","Hearts"));
        cards.add(new Card("A","Hearts"));
        cards.add(new Card("4","Spades"));

        assertEquals("three of a kind", getHand(cards));
        cards.clear();

        //flush
        cards.add(new Card("9","Diamonds"));
        cards.add(new Card("8","Clubs"));
        cards.add(new Card("7","Diamonds"));
        cards.add(new Card("2","Diamonds"));
        cards.add(new Card("6","Diamonds"));
        cards.add(new Card("2","Hearts"));
        cards.add(new Card("J","Diamonds"));

        assertEquals("flush", getHand(cards));
        cards.clear();

        //straight flush
        cards.add(new Card("9","Spades"));
        cards.add(new Card("6","Spades"));
        cards.add(new Card("7","Spades"));
        cards.add(new Card("6","Diamonds"));
        cards.add(new Card("5","Spades"));
        cards.add(new Card("7","Hearts"));
        cards.add(new Card("8","Spades"));

        assertEquals("straight flush", getHand(cards));
        cards.clear();


        //high card
        cards.add(new Card("3","Diamonds"));
        cards.add(new Card("8","Clubs"));
        cards.add(new Card("7","Diamonds"));
        cards.add(new Card("10","Diamonds"));
        cards.add(new Card("A","Hearts"));
        cards.add(new Card("J","Hearts"));
        cards.add(new Card("K","Clubs"));

        assertEquals("high card", getHand(cards));
        cards.clear();


        //full house
        cards.add(new Card("9","Diamonds"));
        cards.add(new Card("7","Clubs"));
        cards.add(new Card("7","Diamonds"));
        cards.add(new Card("10","Diamonds"));
        cards.add(new Card("J","Spades"));
        cards.add(new Card("J","Hearts"));
        cards.add(new Card("J","Clubs"));

        assertEquals("full house", getHand(cards));
        cards.clear();
    }

    @org.junit.jupiter.api.Test
    void getPlayerWithBestHand() {

        // _________________________ 2 players, different hands ________________________________

        Player p2 = new Player("two",200,null);
        Player p3 = new Player("three",100,null);

        List<Card> cards1 = new ArrayList<>();
        List<Card> cards2 = new ArrayList<>();

        //  flush hand
        cards1.add(new Card("4","Diamonds"));
        cards1.add(new Card("J","Diamonds"));
        cards1.add(new Card("8","Diamonds"));
        cards1.add(new Card("2","Diamonds"));
        cards1.add(new Card("9","Diamonds"));

        // four of a kind hand
        cards2.add(new Card("A","Diamonds"));
        cards2.add(new Card("A","Hearts"));
        cards2.add(new Card("A","Clubs"));
        cards2.add(new Card("A","Spades"));
        cards2.add(new Card("J","Diamonds"));

        p2.setAllCards(cards1);
        p3.setAllCards(cards2);

        Cards.setHand(p2);
        Cards.setHand(p3);

        List<Player> players = new ArrayList<>();
        players.add(p2);
        players.add(p3);

        // flush vs four of a kind
        assertEquals(p3.name, Objects.requireNonNull(Cards.getPlayerWithBestHand(players)).get(0).name);

        for (Player p:players) {
            p.hand = "";
        }
        players.clear();
        cards1.clear();
        cards2.clear();



        //_____________________________  3 players, 2 same hand, but different cards _____________________________________________

        Player p4 = new Player("four",200,null);
        List<Card> cards3 = new ArrayList<>();

        //  straight hand 1
        cards1.add(new Card("9","Clubs"));
        cards1.add(new Card("8","Diamonds"));
        cards1.add(new Card("7","Spades"));
        cards1.add(new Card("6","Diamonds"));
        cards1.add(new Card("5","Hearts"));

        //  straight hand 2
        cards2.add(new Card("10","Clubs"));
        cards2.add(new Card("9","Diamonds"));
        cards2.add(new Card("8","Spades"));
        cards2.add(new Card("7","Diamonds"));
        cards2.add(new Card("6","Hearts"));

        // two pair
        cards3.add(new Card("J","Hearts"));
        cards3.add(new Card("A","Hearts"));
        cards3.add(new Card("A","Clubs"));
        cards3.add(new Card("3","Spades"));
        cards3.add(new Card("J","Diamonds"));

        p2.setAllCards(cards1);
        p3.setAllCards(cards2);
        p4.setHandCards(cards3);

        Cards.setHand(p2);
        Cards.setHand(p3);
        Cards.setHand(p4);

        players.add(p2);
        players.add(p3);
        players.add(p4);

        assertEquals(p3.name, Cards.getPlayerWithBestHand(players).get(0).name);

        for (Player p:players) {
            p.hand = "";
        }
        players.clear();
        cards1.clear();
        cards2.clear();
        cards3.clear();


        //_____________________________  3 players, 2 same hand, same cards _____________________________________________


        //  three of a kind hand
        cards1.add(new Card("3","Clubs"));
        cards1.add(new Card("3","Diamonds"));
        cards1.add(new Card("3","Spades"));
        cards1.add(new Card("6","Diamonds"));
        cards1.add(new Card("K","Hearts"));

        //  two pair hand
        cards2.add(new Card("10","Clubs"));
        cards2.add(new Card("8","Diamonds"));
        cards2.add(new Card("8","Spades"));
        cards2.add(new Card("4","Diamonds"));
        cards2.add(new Card("4","Hearts"));

        p2.setAllCards(cards1); // same cards
        p3.setAllCards(cards1);

        p4.setHandCards(cards2);

        Cards.setHand(p2);
        Cards.setHand(p3);
        Cards.setHand(p4);

        players.add(p2);
        players.add(p3);
        players.add(p4);

        Player[] test_winners = new Player[2];
        test_winners[0] = p2;
        test_winners[1] = p3;

        Player[] actual_winners = Objects.requireNonNull(Cards.getPlayerWithBestHand(players)).toArray(new Player[0]);

        // should be 2 winners
        assertEquals(2, Objects.requireNonNull(Cards.getPlayerWithBestHand(players)).size());


        // check if winning names match
        for (int i=0; i<actual_winners.length;i++){
            assertEquals(test_winners[i].name, actual_winners[i].name);
        }

        for (Player p:players) {
            p.hand = "";
        }
        players.clear();
        cards1.clear();
        cards2.clear();
        cards3.clear();

    }


    private String getHand(List<Card> c){
        p1.hand="";
        p1.setAllCards(c);
        Cards.setHand(p1);
        return p1.hand;
    }

}