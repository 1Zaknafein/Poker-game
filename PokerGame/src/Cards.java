import java.util.*;
import java.util.stream.Collectors;

public class Cards {

    private static final String[] cardNames = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private static final String[] cardSuits = {"Clubs", "Diamonds", "Hearts", "Spades"};
    private static final String[] cardHands = {
            "royal flush",
            "straight flush",
            "four of a kind",
            "full house",
            "flush",
            "straight",
            "three of a kind",
            "two pair",
            "pair",
            "high card"};

    public static List<Card> generateDeck(){
        List<Card> deck = new ArrayList<>();

        synchronized (cardNames){
        for (String card_name : cardNames){
            for(String card_suit : cardSuits){
                deck.add(new Card(card_name, card_suit));
            }
        }
        }
        return deck;
    }
    public synchronized static List<Card> shuffleDeck(List<Card> deck){
        Collections.shuffle(deck);
        return deck;
    }

    public synchronized static void setHand(Player player){




        // the function will set players hand cards and what the hand name is
        // need to find the best combination from the given 7 cards

        List<List<Card>> cards = getAllCombinations(player.getAllCards());



        for (List<Card> combination2 : cards){

            Comparator<Card> comparator = Comparator.comparing(Card::getValue).thenComparing(Card::getSuit);
            List<Card> combination = combination2.stream().sorted(comparator).collect(Collectors.toList());

            if(isRoyalFlush(combination)){
                player.hand = cardHands[0];
                player.setHandCards(combination);
                break;
            }
            else if(isStraightFlush(combination)){

                // if first combination to be assigned or player has lower ranking hand
                if(player.hand.equals("") || !player.hand.equals(cardHands[1])){
                    player.hand = cardHands[1];
                    player.setHandCards(combination);
                }

                // if one of the previous combinations was a straight flush
                // need to compare two of them and select the better hand cards
                else { player.setHandCards(compareHands(player, combination)); }

            }
            else if (isFourOfAKind(combination)){

                if(player.hand.equals("")){
                    player.hand = cardHands[2];
                    player.setHandCards(combination);
                }

                else if(player.hand.equals(cardHands[2])){
                    player.setHandCards(compareHands(player,combination));
                }

                else {
                    // need to check if current player hand is better than currently checked combination
                    // basically if current hand has lower index in cardHands[] array

                    int playerHandIndex = Arrays.asList(cardHands).indexOf(player.hand);
                    //int combinationIndex = Arrays.asList(cardHands).indexOf(cardHands[2]); - index 2..

                    // if currently checked combination hand is rank higher (lower index), then replace it
                    if(2 < playerHandIndex){
                        player.hand = cardHands[2];
                        player.setHandCards(combination);
                    }

                }
            }
            else if (isFullHouse(combination)){
                if(player.hand.equals("")){
                    player.hand = cardHands[3];
                    player.setHandCards(combination);
                }

                else if(player.hand.equals(cardHands[3])){
                    player.setHandCards(compareHands(player,combination));
                }

                else {
                    int playerHandIndex = Arrays.asList(cardHands).indexOf(player.hand);
                    if(3 < playerHandIndex){
                        player.hand = cardHands[3];
                        player.setHandCards(combination);
                    }

                }
            }
            else if (isFlush(combination)){
                if(player.hand.equals("")){
                    player.hand = cardHands[4];
                    player.setHandCards(combination);
                }

                else if(player.hand.equals(cardHands[4])){
                    player.setHandCards(compareHands(player,combination));
                }

                else {
                    int playerHandIndex = Arrays.asList(cardHands).indexOf(player.hand);
                    if(4 < playerHandIndex){
                        player.hand = cardHands[4];
                        player.setHandCards(combination);
                    }

                }
            }
            else if (isStraight(combination)){
                if(player.hand.equals("")){
                    player.hand = cardHands[5];
                    player.setHandCards(combination);
                }

                else if(player.hand.equals(cardHands[5])){
                    player.setHandCards(compareHands(player,combination));
                }

                else {
                    int playerHandIndex = Arrays.asList(cardHands).indexOf(player.hand);
                    if(5 < playerHandIndex){
                        player.hand = cardHands[5];
                        player.setHandCards(combination);
                    }

                }
            }
            else if (isThreeOfAKind(combination)){
                if(player.hand.equals("")){
                    player.hand = cardHands[6];
                    player.setHandCards(combination);
                }

                else if(player.hand.equals(cardHands[6])){
                    player.setHandCards(compareHands(player,combination));
                }

                else {
                    int playerHandIndex = Arrays.asList(cardHands).indexOf(player.hand);
                    if(6 < playerHandIndex){
                        player.hand = cardHands[6];
                        player.setHandCards(combination);
                    }

                }
            }
            else if (isTwoPair(combination)){
                if(player.hand.equals("")){
                    player.hand = cardHands[7];
                    player.setHandCards(combination);
                }

                else if(player.hand.equals(cardHands[7])){
                    player.setHandCards(compareHands(player,combination));
                }

                else {
                    int playerHandIndex = Arrays.asList(cardHands).indexOf(player.hand);
                    if(7 < playerHandIndex){
                        player.hand = cardHands[7];
                        player.setHandCards(combination);
                    }

                }
            }
            else if (isPair(combination)){
                if(player.hand.equals("")){
                    player.hand = cardHands[8];
                    player.setHandCards(combination);
                }

                else if(player.hand.equals(cardHands[8])){
                    player.setHandCards(compareHands(player,combination));
                }

                else {
                    int playerHandIndex = Arrays.asList(cardHands).indexOf(player.hand);
                    if(8 < playerHandIndex){
                        player.hand = cardHands[8];
                        player.setHandCards(combination);
                    }

                }
            }
            else {
                if(player.hand.equals("")){
                    player.hand = cardHands[9];
                    player.setHandCards(combination);
                }
                else if(player.hand.equals(cardHands[9])){
                    player.setHandCards(compareHands(player,combination));
                }

            }

        }

        //System.out.println("hand after " + player.hand);

        /*
        if(isRoyalFlush(cards))     {return cardHands[0];}
        if(isStraightFlush(cards))  {return cardHands[1];}
        if(isFourOfAKind(cards))    {return cardHands[2];}
        if(isFullHouse(cards))      {return cardHands[3];}
        if(isFlush(cards))          {return cardHands[4];}
        if(isStraight(cards))       {return cardHands[5];}
        if(isThreeOfAKind(cards))   {return cardHands[6];}
        if(isTwoPair(cards))        {return cardHands[7];}
        if(isPair(cards))           {return cardHands[8];}
        return cardHands[9];

        player.setHandCards(bestHand);
        player.hand = ...;*/

    }

    private static boolean isRoyalFlush(List<Card> c) {

        // A, K, Q, J, 10, all the same suit

        if (containsName(c, "A") &&
                containsName(c, "K") &&
                containsName(c, "Q") &&
                containsName(c, "J") &&
                containsName(c, "10"))
        {
            String suit = c.get(0).suit;
            for (int i = 1; i < c.size(); i++) {
                if (!c.get(i).suit.equals(suit)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    private static boolean isStraightFlush(List<Card> c){

        // five cards in a sequence, all in the same suit

        // firstly check if the same suit
        String suit = c.get(0).suit;
        for (Card card : c){
            if(!card.suit.equals(suit)){
                return false;
            }
        }
        // then check if in sequence - they are already sorted by rank/value

        return c.get(0).value + 1 == c.get(1).value
                && c.get(0).value + 2 == c.get(2).value
                && c.get(0).value + 3 == c.get(3).value
                && c.get(0).value + 4 == c.get(4).value;
    }
    private static boolean isFourOfAKind(List<Card> c){
        //four cards of the same rank
        HashMap<String, Integer> occurrences = new HashMap<>();
        // count how many times each rank/name occurred
        for (Card card : c){
            if(occurrences.containsKey(card.name)){
                occurrences.replace(card.name, occurrences.get(card.name)+1);
            }else {
                occurrences.put(card.name, 1);
            }
        }
        return occurrences.containsValue(4);

    }
    private static boolean isFullHouse(List<Card> c){
        //three cards of a kind with a pair

        HashMap<String, Integer> occurrences = new HashMap<>();
        // count how many times each rank/name occurred
        for (Card card : c){
            if(occurrences.containsKey(card.name)){
                occurrences.replace(card.name, occurrences.get(card.name)+1);
            }else {
                occurrences.put(card.name, 1);
            }
        }
        return occurrences.containsValue(3) && occurrences.containsValue(2);

    }
    private static boolean isFlush(List<Card> c){
        //five cards of the same suit, but not in a sequence
        String s = c.get(0).suit;
        for (Card card : c){
            if (!s.equals(card.suit)){
                return false;
            }
        }
        return true;
    }
    private static boolean isStraight(List<Card> c){

        // five cards in sequence, not of same suit
        // need to account for Ace, as Ace can be low or high - above king and below 2


        for (Card card : c){
            if(card.name.equals("A")){
                // Ace has highest value, and rest of the cards need to have values 2,3,4,5
                if(c.get(0).value == 2 &&
                   c.get(1).value == 3 &&
                   c.get(2).value == 4 &&
                   c.get(3).value == 5){
                        // at this point it has to be 2,3,4,5,ace and since its treated as 'low', counts as straight
                        return true;
                }else {break;}  // else has ace, but different values, so usual check
            }
        }

        return c.get(0).value + 1 == c.get(1).value
                && c.get(0).value + 2 == c.get(2).value
                && c.get(0).value + 3 == c.get(3).value
                && c.get(0).value + 4 == c.get(4).value;
    }
    private static boolean isThreeOfAKind(List<Card> c){
        // three cards of the same name
        HashMap<String, Integer> occurrences = new HashMap<>();
        for (Card card : c){
            if(occurrences.containsKey(card.name)){
                occurrences.replace(card.name, occurrences.get(card.name)+1);
            }else {
                occurrences.put(card.name, 1);
            }
        }
        return occurrences.containsValue(3);
    }
    private static boolean isTwoPair(List<Card> c){
        // count card occurrences
        HashMap<String, Integer> occurrences = new HashMap<>();
        for (Card card : c){
            if(occurrences.containsKey(card.name)){
                occurrences.replace(card.name, occurrences.get(card.name)+1);
            }else {
                occurrences.put(card.name, 1);
            }
        }

        //then check if there are two names with 2 occurrences
        if(occurrences.containsValue(2)){
            int counter = 0;

            for (Map.Entry<String, Integer> entry : occurrences.entrySet()) {
                if ((Integer) ((Map.Entry) entry).getValue() == 2) {
                    counter++;
                }
            }
            return counter == 2;
        }
        return false;
    }
    private static boolean isPair(List<Card> c){
        HashMap<String, Integer> occurrences = new HashMap<>();
        for (Card card : c){
            if(occurrences.containsKey(card.name)){
                occurrences.replace(card.name, occurrences.get(card.name)+1);
            }else {
                occurrences.put(card.name, 1);
            }
        }
        return occurrences.containsValue(2);  // more than one pair is already excluded by calling isTwoPair() function
    }

    private static Card getHighCard(List<Card> c){
        return Collections.max(c);
    }

    public static boolean containsName(final List<Card> cards, final String name){
        return cards.stream().anyMatch(card -> card.getName().equals(name));
    }

    public synchronized static List<Player> getPlayerWithBestHand(List<Player> players){

        List<Player> playersWithSameHand = new ArrayList<>();

        for (String hand : cardHands){

            for (int i=0; i<players.size(); i++){
                if(players.get(i).hand.equals(hand)){
                    playersWithSameHand.add(players.get(i));
                }
            }
            if (!playersWithSameHand.isEmpty()){
                if(playersWithSameHand.size()==1){
                    return playersWithSameHand;
                }
                else{
                //if players same hands, check which cards have higher value

                    List<Player> temp = new ArrayList<>(playersWithSameHand);

                    for ( Player p1 : playersWithSameHand){
                        if (temp.contains(p1)){                         // no need to loop over if this player already lost
                            for (Player p2 : playersWithSameHand){
                                if(temp.contains(p2)){
                                    if(p1!=p2){ // no need to check for the same player..
                                        int p1value = 0;
                                        int p2value = 0;

                                        // not necessary to cover royal flush, cards can only be the same 10-to-ace, so equal

                                        // straight flush
                                        if(p1.hand.equals(cardHands[1])){

                                            // count cards values for p1 and p2
                                            // only need rightmost value, since sorted by value, lowest to highest

                                            //for (Card c : p1.getHandCards()){ p1value += c.value; }
                                            //for (Card c : p2.getHandCards()){ p2value += c.value; }

                                            p1value = p1.getHandCards().get(4).value;
                                            p2value = p2.getHandCards().get(4).value;


                                            // else same values, both winners
                                        }

                                        // four of a kind
                                        else if(p1.hand.equals(cardHands[2])){

                                            // find value of the 4-card  combo

                                            // since the cards are sorted, can simply take value of the middle card;
                                            // the 'kicker' is on either end

                                            p1value = p1.getHandCards().get(2).value;
                                            p2value = p2.getHandCards().get(2).value;

                                        }

                                        // full house
                                        else if(p1.hand.equals(cardHands[3])){

                                            // firstly ranked by rank of triplet, then by rank of pair

                                            p1value = p1.getHandCards().get(2).value;
                                            p2value = p2.getHandCards().get(2).value;

                                            if (p1value==p2value) {

                                                // check if p1value equals card value on pos 0; if not then the first two cards are the pair

                                                if(p1value == p1.getHandCards().get(0).value){
                                                    p1value = p1.getHandCards().get(4).value;
                                                }else {
                                                    p1value = p1.getHandCards().get(0).value;
                                                }

                                                if(p2value == p2.getHandCards().get(0).value){
                                                    p2value = p2.getHandCards().get(4).value;
                                                }else {
                                                    p2value = p2.getHandCards().get(0).value;
                                                }
                                            }

                                        }

                                        // flush
                                        else if(p1.hand.equals(cardHands[4])){

                                            // highest card wins, if equal, next highest

                                            p1value = p1.getHandCards().get(4).value; // starting from highest
                                            p2value = p2.getHandCards().get(4).value;

                                            for(int i=3; i>=0; i--){
                                                if(p1value==p2value){
                                                    p1value = p1.getHandCards().get(i).value;
                                                    p2value = p2.getHandCards().get(i).value;
                                                }else {break;}
                                            }

                                        }

                                        // straight
                                        else if(p1.hand.equals(cardHands[5])){
                                            // take last value since its sorted
                                            p1value = p1.getHandCards().get(4).value;
                                            p2value = p2.getHandCards().get(4).value;

                                        }

                                        // three of a kind
                                        else if(p1.hand.equals(cardHands[6])){

                                            // need to find three cards of same value and compare to other players hand
                                            // the middle card will always belong to the 'triplet'

                                            p1value = p1.getHandCards().get(2).value; // middle card
                                            p2value = p2.getHandCards().get(2).value;

                                            if(p1value==p2value){
                                                // the triplet can start on first, second or third card
                                                // so the kicker can be first, second or last card

                                                // so will count number of occurrences, if not 3 then can compare kickers

                                                // for p1
                                                HashSet<Integer> p1kickers = new HashSet<>();
                                                HashSet<Integer> p2kickers = new HashSet<>();

                                                // add kickers to set - values not 'triplet'
                                                // p1
                                                for (Card c : p1.getHandCards()){
                                                    if(c.value!=p1value){
                                                        p1kickers.add(c.value);
                                                    }
                                                }

                                                // p2
                                                for (Card c : p2.getHandCards()){
                                                    if(c.value!=p2value){
                                                        p2kickers.add(c.value);
                                                    }
                                                }

                                                // find max values / highest kickers

                                                p1value = Collections.max(p1kickers);
                                                p2value = Collections.max(p2kickers);

                                                if (p1value==p2value){
                                                    // it may happen that they are still the same
                                                    // so just take the other kicker for comparison
                                                    p1value = Collections.min(p1kickers);
                                                    p2value = Collections.min(p2kickers);
                                                }
                                            }

                                        }

                                        // two pair
                                        else if(p1.hand.equals(cardHands[7])){

                                            // find which cards are the 2 pairs

                                            HashMap<Integer, Integer> p1map = new HashMap<>();
                                            HashMap<Integer, Integer> p2map = new HashMap<>();
                                            HashSet<Integer> p1pairs = new HashSet<>();
                                            HashSet<Integer> p2pairs = new HashSet<>();
                                            int p1kicker = -1;
                                            int p2kicker = -1;

                                            // add values and their number of occurrences to map
                                            for (Card card : p1.getHandCards()){
                                                if(p1map.containsKey(card.value)){
                                                    p1map.replace(card.value, p1map.get(card.value)+1);
                                                }else {
                                                    p1map.put(card.value, 1);
                                                }
                                            }
                                            for (Card card : p2.getHandCards()){
                                                if(p2map.containsKey(card.value)){
                                                    p2map.replace(card.value, p2map.get(card.value)+1);
                                                }else {
                                                    p2map.put(card.value, 1);
                                                }
                                            }

                                            // if number of occurrences was 2, add that value to set
                                            for (Map.Entry<Integer, Integer> entry : p1map.entrySet()) {
                                                if (entry.getValue().equals(2)) {
                                                    p1pairs.add(entry.getKey());
                                                }else {
                                                    p1kicker = entry.getKey();
                                                }
                                            }
                                            for (Map.Entry<Integer, Integer> entry : p2map.entrySet()) {
                                                if (entry.getValue().equals(2)) {
                                                    p2pairs.add(entry.getKey());
                                                }else {
                                                    p2kicker = entry.getKey();
                                                }
                                            }

                                            // get highest pair values and compare
                                            p1value = Collections.max(p1pairs);
                                            p2value = Collections.max(p2pairs);

                                            // if highest pair value is the same, use second pair
                                            // or kicker if needed
                                            if(p1value == p2value){
                                                p1value = Collections.min(p1pairs);
                                                p2value = Collections.min(p2pairs);

                                                if (p1value == p2value){
                                                    p1value = p1kicker;
                                                    p2value = p2kicker;
                                                }
                                            }
                                        }

                                        // pair
                                        else if (p1.hand.equals(cardHands[8])){

                                            HashMap<Integer, Integer> p1map = new HashMap<>();
                                            HashMap<Integer, Integer> p2map = new HashMap<>();

                                            //use list instead of hashset, due to hashset having no order of elements
                                            List<Integer> p1kickers = new ArrayList<>();
                                            List<Integer> p2kickers = new ArrayList<>();

                                            int p1pair = -1;
                                            int p2pair = -1;

                                            // add values and their number of occurrences to map
                                            for (Card card : p1.getHandCards()){
                                                if(p1map.containsKey(card.value)){
                                                    p1map.replace(card.value, p1map.get(card.value)+1);
                                                }else {
                                                    p1map.put(card.value, 1);
                                                }
                                            }
                                            for (Card card : p2.getHandCards()){
                                                if(p2map.containsKey(card.value)){
                                                    p2map.replace(card.value, p2map.get(card.value)+1);
                                                }else {
                                                    p2map.put(card.value, 1);
                                                }
                                            }

                                            // if number of occurrences was 2, its the pair
                                            // else kickers

                                            for (Map.Entry<Integer, Integer> entry : p1map.entrySet()) {
                                                if (entry.getValue().equals(2)) {
                                                    p1pair = entry.getKey();
                                                }else {
                                                    p1kickers.add(entry.getKey());
                                                }
                                            }
                                            for (Map.Entry<Integer, Integer> entry : p2map.entrySet()) {
                                                if (entry.getValue().equals(2)) {
                                                    p2pair = entry.getKey();
                                                }else {
                                                    p2kickers.add(entry.getKey());
                                                }
                                            }

                                            if (p1pair==p2pair){

                                                // if pairs the same, use kickers
                                                // sorting in descending order will make it easier

                                                Collections.sort(p1kickers);
                                                Collections.sort(p2kickers);
                                                Collections.reverse(p1kickers);
                                                Collections.reverse(p2kickers);

                                                // now with having sorted list

                                                for (int i=0; i<p1kickers.size(); i++){
                                                    p1value = p1kickers.get(i);
                                                    p2value = p2kickers.get(i);
                                                    if(p1value!=p2value){break;}
                                                }


                                            }
                                            else {
                                                p1value = p1pair;
                                                p2value = p2pair;
                                            }

                                        }

                                        // high card
                                        else if (p1.hand.equals(cardHands[9])){
                                            for (int i=4; i>=0; i--){
                                                p1value = p1.getHandCards().get(i).value;
                                                p2value = p2.getHandCards().get(i).value;
                                                if (p1value!=p2value){break;}
                                            }
                                        }

                                        if(p1value>p2value)         { if(temp.contains(p2)){temp.remove(p2);} }
                                        else if (p1value<p2value)   { if(temp.contains(p1)){temp.remove(p1);} }

                                    }
                                }

                            }
                        }

                    }

                    return temp;
                    //return winning player(s)
            }
            }


        }



        return null;
    }

    public static List<Card> sortCards(List<Card> cards){
        // sort cards by value (2, 6, Queen, King..)
        // and then sort by suit (clubs, hearts..)
        Comparator<Card> comparator = Comparator.comparing(Card::getValue).thenComparing(Card::getSuit);
        return cards.stream().sorted(comparator).collect(Collectors.toList());
    }

    private static List<List<Card>> getAllCombinations(List<Card> elements) {

        List<List<Card>> combinations = new ArrayList<>();

        for (int i = 1; i < Math.pow(2, elements.size()); i++) {

            List<Card> temp = new ArrayList<>();
            for (int j = 0; j < elements.size(); j++ ) {
                if ((i & (int) Math.pow(2, j)) > 0){
                    temp.add(elements.get(j));
                }
            }
            if(temp.size()==5){
                combinations.add(temp);
            }
        }
        return combinations;
    }
    private static List<Card> compareHands(Player player, List<Card> combination){


        Player tempPlayer1 = new Player("a", 0, null);
        Player tempPlayer2 = new Player("b", 0, null);

        tempPlayer1.hand = player.hand;
        tempPlayer2.hand = player.hand;

        tempPlayer1.setHandCards(player.getHandCards());    // the hand already assigned to the player
        tempPlayer2.setHandCards(combination);              // the new hand combination

        List<Player> temp = new ArrayList<>();

        temp.add(tempPlayer1);
        temp.add(tempPlayer2);

        temp = getPlayerWithBestHand(temp);     // returns a list of players with the best hand;
                                                // normally should return size 1

        if(temp!=null){
            return temp.get(0).getHandCards();
        }else {
            System.out.println("temporary list for " + player.hand + " was null");
            return null;
        }
    }


}

