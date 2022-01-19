import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {
    final String name;
    private float money;
    private List<Card> cards;       // 2 self cards
    private List<Card> allCards;    // 7 cards - community + self cards
    private List<Card> handCards;   // 5 cards, best hand
    String hand;
    public final Handler handler;
    public Table table;
    public AtomicInteger action;
    public boolean ready = false;
    public float bet = 0;
    public float raiseTo = 0;
    public Game game;

    // action 1: call
    // action 2: raise
    // action 3: fold
    // action 4:

    public Player(String name, float money, Handler handler){
        this.name = name;
        this.money = money;
        cards = new ArrayList<>();
        allCards = new ArrayList<>();
        handCards = new ArrayList<>();
        hand = "";
        this.handler = handler;
        action = new AtomicInteger(0);
    }

    public void addCard(Card card){
        cards.add(card);
    }
    public List<Card> getCards(){
        return cards;
    }

    public void setHandCards(List<Card> c){
        if(handCards==null){handCards = new ArrayList<>();}
        if(!handCards.isEmpty()){handCards.clear();}
        handCards.addAll(c);
    }
    public void setAllCards(List<Card> c){
        if(allCards==null){allCards = new ArrayList<>();}
        if(!allCards.isEmpty()){allCards.clear();}
        allCards.addAll(c);
    }

    public void resetData(){
        cards.clear();
        allCards.clear();
        handCards.clear();
        //table = null;
        hand = "";
        action.set(0);
        raiseTo = 0;
        bet = 0;
        ready = false;
        game=null;
    }

    public List<Card> getAllCards(){
        return allCards;
    }

    public List<Card> getHandCards(){
        return handCards;
    }

    public float getMoney(){return money;}
    public void giveMoney(float amount){
        money += amount;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
