package net.ypaaxx.blackjack;

import java.util.ArrayList;
import java.util.Random;

class Pack {
    private ArrayList<Card> pack = new ArrayList<Card>(52);

    public Pack() {
        pack.clear();
        char suits[]={'♥', '♦', '♠', '♣'}; // hearts dimonds spades clubs
        char ranks[]={'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
        for (char suit: suits){
            for (char rank: ranks){
                pack.add(new Card(rank, suit));
            }
        }
    }

    public Card getRandomCard(){
        Random random = new Random();
        int numCard = random.nextInt(pack.size());
        return pack.remove(numCard);
    }

    public Card getCard(char rank, char suit){
        if (numerOfCards() < 52/3) {
            System.out.println("Колода перемешана");
        }
        for (Card card: pack){
            if (card.getRank() == rank && card.getSuit() == suit) {
                pack.remove(card);
                return card;
            }
        }
        return null;
    }

    public Card getCard(char rank){
        for (Card card: pack){
            if (card.getRank() == rank) pack.remove(card);
            return card;
        }
        return null;
    }

    public int numerOfCards(){
        return pack.size();
    }
}
