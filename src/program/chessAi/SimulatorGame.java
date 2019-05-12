package program.chessAi;

import program.Chess;
import program.ChessGameTemplate;

import java.util.*;

public class SimulatorGame extends ChessGameTemplate {

    private Deque<MoveRecord> moveRecords = new ArrayDeque<>();


    private static final Map<Integer, Integer> WEIGHTS = new HashMap<Integer, Integer>() {{
        put(Chess.JIANG, 100);
        put(Chess.SHI, 8);
        put(Chess.XIANG, 7);
        put(Chess.MA, 13);
        put(Chess.JV, 19);
        put(Chess.PAO, 15);
        put(Chess.ZU, 5);
    }};

    SimulatorGame(ChessGameTemplate chessGame, boolean isAi) {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                chessboard[r][c] = chessGame.getChessAt(r, c);
            }
        }
        redTurn = !isAi;
    }

    public Deque<MoveRecord> getMoveRecords() {
        return moveRecords;
    }

    void move(int row, int col) {
        if (hintsBoard[row][col]) {
//            Chess atPos = chessboard[row][col];
            Chess moveChess = chessboard[selectR][selectC];
            moveChess.deSelect();
            chessboard[row][col] = moveChess;
            chessboard[selectR][selectC] = null;
            selectR = -1;
            selectC = -1;
            redTurn = !redTurn;
            clearHints();
        } else {
            System.out.println("wtf");
        }
    }

    void move(Move move) {
        int[] srcPos = move.getSrcPos();
        int[] destPos = move.getDestPos();
        Chess src = chessboard[srcPos[0]][srcPos[1]];
        Chess dest = chessboard[destPos[0]][destPos[1]];
        moveRecords.addLast(new MoveRecord(move, src, dest));
        chessboard[destPos[0]][destPos[1]] = src;
        chessboard[srcPos[0]][srcPos[1]] = null;
    }

    void undoLastMove() {
        MoveRecord record = moveRecords.removeLast();
        int[] srcPos = record.getMove().getSrcPos();
        int[] destPos = record.getMove().getDestPos();
        chessboard[srcPos[0]][srcPos[1]] = record.getSrcChess();
        chessboard[destPos[0]][destPos[1]] = record.getDestChess();
    }

    int evaluate() {
        int sum = 0;
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                Chess chess = chessboard[r][c];
                if (chess != null) {
                    int weight = WEIGHTS.get(chess.getRep());
                    sum += chess.isRed() ? -weight : weight;
                }
            }
        }
        return sum;
    }

    void setSide(boolean isRedMoving) {
        redTurn = isRedMoving;
    }

//    private boolean selectable(int row, int col) {
//        Deque<Chess> deq = chessDeque[row][col];
//        if (!deq.isEmpty()) {
//            Chess chess = deq.getLast();
//            return chess.isRed() ^ (!redTurn);
//        }
//        return false;
//    }

//    private boolean movable(int srcRow, int srcCol, int destRow, int destCol) {
//
//    }

    Queue<Move> getAllPossibleMoves() {
        Queue<Move> moves = new ArrayDeque<>();
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                if (selectPosition(r, c)) {
                    // hints are refreshed
                    for (int r1 = 0; r1 < 10; r1++) {
                        for (int c1 = 0; c1 < 9; c1++) {
                            if (getHintAt(r1, c1)) {
                                moves.add(new Move(new int[]{r, c}, new int[]{r1, c1}));
                            }
                        }
                    }
                    getChessAt(r, c).deSelect();
                }
                clearHints();
            }
        }
        return moves;
    }
}
