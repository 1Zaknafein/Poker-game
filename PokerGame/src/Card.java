class Card implements Comparable<Card> {

    final String name;
    final String suit;
    int value;
    char suitSymbol;

    public String getName() {
        return name;
    }

    public String getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    public Card(String name, String suit){
        this.name = name;
        this.suit = suit;
        switch (name) {
            case "A":
                value = 14;
                break;
            case "K":
                value = 13;
                break;
            case "Q":
                value = 12;
                break;
            case "J":
                value = 11;
                break;
            default:
                try {
                    value = Integer.parseInt(name);
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid card name!");
                }
        }

        switch (this.suit) {
            case "Diamonds" -> suitSymbol = '♦';
            case "Hearts" -> suitSymbol = '♥';
            case "Clubs" -> suitSymbol = '♣';
            case "Spades" -> suitSymbol = '♠';
        }

    }


    @Override
    public String toString() {
        return name + suitSymbol;
    }

    @Override
    public int compareTo(Card c) {
        if (this.getValue() > c.getValue()) {
            return 1;
        } else if (this.getValue() < c.getValue()) {
            return -1;
        }
        return 0;
    }
}
