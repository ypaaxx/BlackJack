package net.ypaaxx.cards;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Класс игрока как он есть
 */
public class Gamer extends Thread {

    /** Счётчик безымянных игроков */
    private static int numGamer;

    /** Средства на счету */
    protected int bankroll;

    /** Рука */
    protected Hand hand;

    /** Ставка */
    protected int bet;

    /** Сокет соеденения юзера */
    private Socket socket;

    /** Сканер комманд юзера */
    protected Scanner in;

    /** поток сообщений юзеру */
    protected PrintWriter out;

    /** Конструктор с именем и сокетом
     *
     * @param name      имя игрока
     * @param incoming  сокет с юзером
     */
    public Gamer(String name, Socket incoming){
        super(name + ++numGamer);
        try {
            socket = incoming;
            incoming.setSoTimeout(30000);
            in = new Scanner(socket.getInputStream());
            out = new  PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){

        }
        bankroll = 1000;
    }


    public int getBankroll(){
        return bankroll;
    }

    public void exit(){
        hand = null;
        bet = 0;
        try {
            socket.close();
        }catch (Exception e){

        }
    }

    public void sendText(String str){
        out.println(str);
    }

    public Hand getHand(){
        return hand;
    }

    public void takeCard(Card card){
        this.hand.add(card);
    }



}
