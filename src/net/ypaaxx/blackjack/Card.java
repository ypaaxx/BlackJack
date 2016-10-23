package net.ypaaxx.blackjack;

class Card {
    private char suit;
    private char rank;
    private int points;

    public Card(char rank, char suit){
        this.rank=rank;
        this.suit=suit;
    }

    public char getRank(){	return this.rank;	}

    public char getSuit(){	return this.suit;	}

    public String toString(){
        StringBuilder strCard = new StringBuilder();
        strCard.append(rank);
        strCard.append(suit);
        return strCard.toString();
    }

    public boolean equals(Object otherObj){
        if(this == otherObj) return false;
        if(otherObj == null) return false;
        if(this.getClass() != otherObj.getClass()) return false;

        Card other = (Card) otherObj;

        return this.rank == other.getRank() && this.suit == other.getSuit();
    }
}
