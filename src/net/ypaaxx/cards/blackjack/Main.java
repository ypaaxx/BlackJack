package net.ypaaxx.cards.blackjack;

import java.util.*;

public class Main {

    private static ArrayList<BJGamer> players;
    private static final int NUMBER_OF_PLAYER = 4;

    public static void main(String[] args) {

        players = new ArrayList<BJGamer>(NUMBER_OF_PLAYER);

        BJDiller diller = new BJDiller(players);
        players.add(new BJGamer("gamer1", diller));
        players.add(new BJGamer("gamer2", diller));

    }
}

