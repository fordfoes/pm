package org.example;

import java.util.Scanner;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new GoodDealer();
        Board board = dealer.dealCardsToPlayers();
        /* System.out.println(board.toString());
        System.out.println("Нажмите Enter, чтобы продолжить...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();*/

        board = dealer.dealFlop(board);

        board = dealer.dealTurn(board);

        board = dealer.dealRiver(board);

        System.out.println(board.toString());

        try {
            PokerResult result = dealer.decideWinner(board);
            System.out.println("Результат: " + result);
        } catch (InvalidPokerBoardException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}