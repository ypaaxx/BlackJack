package net.ypaaxx.cards;

import java.util.LinkedList;

public class Hand implements Comparable <Hand> {
    private LinkedList<Card> cards = new LinkedList<Card>();
    private int aces;
    private int numCard;

    public void add(Card card){
        cards.add(card);
        numCard++;
        if (card.getRank() == 'A') aces++;
    }

    public Card getLast(){
        return cards.peekLast();
    }

    public int getPoints(){
        int points=0;
        int aces = this.aces;
        if (numCard == 0) return 0;
        for (Card card: cards) {
            char rank = card.getRank();
            if (rank>'1' && rank<='9') points += Character.getNumericValue(rank);
            else if (  rank == 'T'
                    || rank == 'J'
                    || rank == 'Q'
                    || rank == 'K'
                    ) points += 10;
            else if (rank == 'A') points += 11;
        }
        while (points>21 && aces>0) {
            points -= 10;
            aces--;
        }
        return points;
    }

    public boolean isBlackJack(){
        if(numCard == 2 & getPoints() == 21) return true;
        return false;
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        if(this.numCard == 0) return null;
        for (Card card: cards) str.append(card);
        return str.toString();
    }

    @Override
    public int compareTo(Hand otherHand){
        if (getPoints()>21) return -1;
        if (otherHand.getPoints() > 21) return 1;
        return getPoints() - otherHand.getPoints();
    }
}
