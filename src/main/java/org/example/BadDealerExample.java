package org.example;

/**
 * Пример плохого дилера - он раздает повторяющиеся карты и всегда определяет исход как ничью
 */
public class BadDealerExample implements Dealer {
    @Override
    public Board dealCardsToPlayers() {
        return new Board("2C2H", "2C2H", null, null, null);
    }

    @Override
    public Board dealFlop(Board board) {
        return new Board("2C2H", "2C2H", "2C2H2C", null, null);
    }

    @Override
    public Board dealTurn(Board board) {
        return new Board("2C2H", "2C2H", "2C2H2C", "2C", null);
    }

    @Override
    public Board dealRiver(Board board) {
        return new Board("2C2H", "2C2H", "2C2H2C", "2C", "2H");
    }

    @Override
    public PokerResult decideWinner(Board board) {
        return PokerResult.DRAW;
    }
}
