package net.ypaaxx.cards;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

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

    protected Timer timer;

    /** Выход при задержке юзера */
    public class Timeout extends TimerTask{
        @Override
        public void run(){
            System.out.println("Таймер!");
            exit();
        }
    }

    /** Конструктор с именем и сокетом
     *
     * @param name      имя игрока
     * @param incoming  сокет с юзером
     */
    public Gamer(String name, Socket incoming){
        super(name + ++numGamer);
        try {
            socket = incoming;
            in = new Scanner(socket.getInputStream());
            out = new  PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){
            exit();
        }
        bankroll = 1000;
    }

    /** Авторизация юзера по базе
     *
     * @param login Имя юзера в базе
     * @param pass  Пароль
     * @return      Успешность автризации
     */
    private boolean authorization(String login, String pass){

        return false;
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
