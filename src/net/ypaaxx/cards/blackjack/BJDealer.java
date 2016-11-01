package net.ypaaxx.cards.blackjack;

import net.ypaaxx.cards.Card;
import net.ypaaxx.cards.Hand;
import net.ypaaxx.cards.Pack;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

class BJDealer extends Thread {

    private Pack pack;
    private Hand hand;
    private CyclicBarrier endGame;
    private Phaser phaser;
    private LinkedList<BJGamer> players;
    private LinkedList<BJGamer> waiting;

    public BJDealer(LinkedList<BJGamer> waiting) {
        super("dealer");

        this.waiting = waiting;
        players = new LinkedList<BJGamer>();
        pack = new Pack();
        start();
    }

    public LinkedList<BJGamer> getPlayers(){
        return players;
    }

    public void setPhaser(int i){
        phaser = new Phaser(i);
    }

    public void arrivePhaser(boolean await) {
        if (await) phaser.arriveAndAwaitAdvance();
        else phaser.arriveAndDeregister();
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
                    player.sendText(getName() + " hand:  *" + hand.getLast());
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
        phaser = new Phaser(players.size()+1);

        //Диллер открывает свою карту и наберает руку
        while (hand.getPoints() < 17) {
            hand.add(pack.getRandomCard());
            System.out.println(getName() + " hand: " + hand + " (" + hand.getPoints() + ")");
        }

        //Результаты игры
        for(BJGamer player:players)
            player.sendText(getName() + ": " + hand + " (" + hand.getPoints() + ")");
        synchronized (players) {
            for (BJGamer player : players) {
                synchronized (player) {
                    player.payTime(player.getHand().compareTo(hand));
                    if (player.getHand().compareTo(hand) > 0)
                        player.sendText("Win! " + " (" + player.getBankroll() + ")" + "\n");
                    else if (player.getHand().compareTo(hand) == 0)
                        player.sendText("Push " + " (" + player.getBankroll() + ")" + "\n");
                    else if (player.getHand().compareTo(hand) < 0)
                        player.sendText("Lose " + " (" + player.getBankroll() + ")" + "\n");
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
        while(true) {
            do {
                for (BJGamer player : players) {
                    if (!player.isAlive()) player.start();
                }
                setPhaser(players.size()+1);
                if (!players.isEmpty()) game();
                while (players.size() < 4 & !waiting.isEmpty()) {
                    players.add(waiting.pollFirst());
                }
            } while (!players.isEmpty());
            try{
                sleep(5000);
            }catch (InterruptedException e){

            }
        }
    }
}
