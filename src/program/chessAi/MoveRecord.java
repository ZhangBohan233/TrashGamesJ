package program.chessAi;

import program.Chess;

public class MoveRecord {

    private Move move;
    private Chess srcChess, destChess;

    MoveRecord(Move move, Chess srcChess, Chess destChess) {
        this.move = move;
        this.srcChess = srcChess;
        this.destChess = destChess;
    }

    public Move getMove() {
        return move;
    }

    public Chess getDestChess() {
        return destChess;
    }

    public Chess getSrcChess() {
        return srcChess;
    }
}
