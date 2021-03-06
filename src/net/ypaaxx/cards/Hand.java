package net.ypaaxx.cards;

import java.util.LinkedList;

public class Hand implements Comparable <Hand> {
    private LinkedList<Card> cards = new LinkedList<Card>();
    private int aces;
    private int points;

    public void add(Card card){
        cards.add(card);
        if (card.getRank() == 'A') aces++;
        setPoints();
    }

    public Card getLast(){
        return cards.peekLast();
    }

    private void setPoints(){
        points=0;
        int aces = this.aces;
        if (cards.size() == 0) return;
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
        return;
    }

    public int getPoints(){
        return points;
    }

    public boolean isBlackJack(){
        if(cards.size() == 2 & getPoints() == 21) return true;
        return false;
    }

    public String toString(){
        StringBuilder str = new StringBuilder();
        if(cards.size() == 0) return null;
        for (Card card: cards) str.append(card);
        return str.toString();
    }

    @Override
    public int compareTo(Hand otherHand){
        if (getPoints()>21) return -1;
        else if (otherHand.getPoints() > 21) return 1;
        else if (isBlackJack() && !otherHand.isBlackJack()) return 1;
        return getPoints() - otherHand.getPoints();
    }
}
