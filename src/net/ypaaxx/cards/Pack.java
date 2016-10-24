package net.ypaaxx.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Pack {
    private ArrayList<Card> pack;
    private Random random;

    public Pack() {
        pack = new ArrayList<Card>(52);
        shuffleNewPack();
        random = new Random();
    }

    private void shuffleNewPack(){
        pack.clear();
        char suits[]={'♥', '♦', '♠', '♣'}; // hearts dimonds spades clubs
        char ranks[]={'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
        for (char suit: suits){
            for (char rank: ranks){
                pack.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(pack);
    }

    private void shuffleIfNeed(){
        if (numerOfCards() < 52/3) {
            shuffleNewPack();
            System.out.println("Pack suffled");
        }
    }

    public Card getRandomCard(){
        shuffleIfNeed();
        int numCard = random.nextInt(pack.size());
        return pack.remove(numCard);
    }

    public Card getCard(char rank, char suit){
        shuffleIfNeed();
        for (Card card: pack){
            if (card.getRank() == rank && card.getSuit() == suit) {
                pack.remove(card);
                return card;
            }
        }
        return null;
    }

    public Card getCard(char rank){
        shuffleIfNeed();

        for (Card card: pack){
            if (card.getRank() == rank) pack.remove(card);
            return card;
        }
        return null;
    }

    private int numerOfCards(){
        return pack.size();
    }
}
