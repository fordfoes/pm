package org.example;

public class PokerGame {
    public static void main(String[] args) {
        Dealer dealer = new BadDealerExample();
        Board board = dealer.dealCardsToPlayers();

        System.out.println(board.toString());
    }
}
