package net.ypaaxx.cards.blackjack;

import net.ypaaxx.cards.Hand;
import net.ypaaxx.cards.Pack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

public class BJDiller extends Thread {

    private Pack pack;
    private Hand hand;
    private CyclicBarrier endGame;
    private Phaser phaser;
    private ArrayList<BJGamer> players;

    public BJDiller(ArrayList<BJGamer> players) {
        super("diller");
        phaser = new Phaser(3);
        this.players = players;
        pack = new Pack();
        start();
    }

    public ArrayList<BJGamer> getPlayers(){
        return players;
    }

    public Phaser getPhaser(){
        return phaser;
    }

    public CyclicBarrier getEndGame(){
        return endGame;
    }

    private boolean isAllDone(List<BJGamer> players){
        boolean allDone = true;
        for (BJGamer player:players) {
            allDone = allDone & player.isDone();
        }
        return allDone;
    }

    private void game(){
        //0 фаза
        //Игроки делают ставки или уходят
        hand = new Hand();
        phaser.arriveAndAwaitAdvance();

        //1 фаза
        //Состав игроков сформирован
        //Диллер раздает карты, пока игроки ожидают
        System.out.println(getName() + ": start");
        endGame = new CyclicBarrier(players.size()+1);

        hand.add(pack.getRandomCard());
        hand.add(pack.getRandomCard());
        System.out.println(getName() + ": " + " *" + hand.getLast());
        synchronized (players) {
            for (BJGamer player : players) {
                synchronized (player) {
                    player.takeCard(pack.getRandomCard());
                    player.takeCard(pack.getRandomCard());
                    player.notify();
                }
            }
        }

        do {
            phaser.arriveAndAwaitAdvance();

            //2n фаза
            //Диллер раздает карты игрокам, которые ещё не закончили игру
            synchronized (players) {
                for (BJGamer player : players) {
                    synchronized (player) {
                        if (!player.isDone()) {
                            player.takeCard(pack.getRandomCard());
                            player.notify();
                        }
                    }
                }
            }
        } while (!isAllDone(players));

        //Формирование фазера для новой игры
        phaser = new Phaser(1);

        //Диллер открывает свою карту и наберает руку
        System.out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
        while (hand.getPoints() < 17) {
            hand.add(pack.getRandomCard());
            System.out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
        }

        //Результаты игры
        synchronized (players) {
            for (BJGamer player : players) {
                synchronized (player) {
                    player.payTime(player.getHand().compareTo(hand));
                    if (player.getHand().compareTo(hand) > 0)
                        System.out.println(getName() + ": выйграл игрок " + player.getName() + " (" + player.getBankroll() + ")");
                    else if (player.getHand().compareTo(hand) == 0)
                        System.out.println(getName() + ": ничья с " + player.getName() + " (" + player.getBankroll() + ")");
                    else if (player.getHand().compareTo(hand) < 0)
                        System.out.println(getName() + ": игрок проиграл " + player.getName() + " (" + player.getBankroll() + ")");
                    player.notify();
                }
            }
        }

        try{
            endGame.await();
        }catch (Exception e){

        }
    }

    @Override
    public void run() {
        do {
            game();

        }while (!players.isEmpty());
    }
}
