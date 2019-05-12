package program.chessAi;

import program.ChessGame;

import java.util.*;

public class ChessAI {

    private int limit;

    /**
     * Whether this AI controls the red side.
     */
    private boolean isRed;

    private Map<Move, Integer> posMaxMap = new HashMap<>();

    private Map<Integer, List<Move>> moveMap = new HashMap<>();

    public ChessAI(int limit, boolean isRed) {
        this.limit = limit;
        this.isRed = isRed;
    }

    public boolean isRed() {
        return isRed;
    }

//    public Move move(ChessGame game) {
//        moveMap.clear();
//        alphaBeta(new SimulatorGame(game, true), limit, -100000, 100000, true, new ArrayList<>());
//        int max = -100000;
//        List<Move> maxMoveSequence = null;
//        for (Map.Entry<Integer, List<Move>> entry : moveMap.entrySet()) {
////            if (entry.getValue() == best) {
////                return entry.getKey();
////            }
//            if (entry.getKey() > max) {
//                max = entry.getKey();
//                maxMoveSequence = entry.getValue();
//                System.out.print(max);
//                System.out.println(maxMoveSequence);
//            }
//        }
//        System.out.println();
//        assert maxMoveSequence != null;
//        return maxMoveSequence.get(0);
////        throw new RuntimeException("No move can be made");
//    }

    public Move move(ChessGame game) {
        posMaxMap.clear();
        int best = alphaBeta(new SimulatorGame(game, true), limit, -100000, 100000, true, new ArrayList<>());
        int max = -100000;
        Move maxMove = null;
        for (Map.Entry<Move, Integer> entry : posMaxMap.entrySet()) {
//            if (entry.getValue() == best) {
//                return entry.getKey();
//            }
            System.out.print(max);
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxMove = entry.getKey();
            }
        }
        System.out.println();
        assert maxMove != null;
        return maxMove;
//        throw new RuntimeException("No move can be made");
    }

    private int alphaBeta(SimulatorGame simulator, int depth, int alpha, int beta, boolean isAiMoving, List<Move> moveSequence) {
        simulator.setSide(!isAiMoving);
        if (depth == 0) {
            int val = simulator.evaluate();
//            moveMap.put(val, moveSequence);
////            posMaxMap.put(leadingMove, val);
            return val;
        }
        Queue<Move> moves = simulator.getAllPossibleMoves();
        while (!moves.isEmpty()) {
            Move nextMove = moves.remove();
            simulator.move(nextMove);

            List<Move> newSeq = new ArrayList<>(moveSequence);
            newSeq.add(nextMove);
            int val = -alphaBeta(simulator, depth - 1, -beta, -alpha, !isAiMoving, newSeq);
            simulator.undoLastMove();

            if (depth == limit) {
                System.out.println(val);
                posMaxMap.put(nextMove, val);
            }

            if (val >= beta) {
                return beta;
            }
            if (val > alpha) {
                alpha = val;
            }
        }
        return alpha;
    }


//    private void recursiveSimulate(SimulatorGame simulator, int depth, boolean isRedTurn, int sum, AiMove firstMove) {
//        if (depth >= limit) {
//            Integer maxOfThis = posMaxMap.get(firstMove);
//            if (maxOfThis == null || sum > maxOfThis) {
//                posMaxMap.put(firstMove, sum);
//            }
//        } else {
//            for (int r = 0; r < 10; r++) {
//                for (int c = 0; c < 9; c++) {
//                    if (simulator.selectPosition(r, c)) {
//                        // hints are refreshed
//                        for (int r1 = 0; r1 < 10; r1++) {
//                            for (int c1 = 0; c1 < 9; c1++) {
//                                if (simulator.getHintAt(r1, c1)) {
//                                    if (depth == 0) firstMove = new AiMove(new int[]{r, c}, new int[]{r1, c1});
//                                    SimulatorGame child = simulator.makeSimulator(isRedTurn);
//                                    child.selectPositionNative(r, c);
//                                    int result = child.move(r1, c1, !isRedTurn);
//                                    recursiveSimulate(child, depth + 1, !isRedTurn, result + sum, firstMove);
//                                }
//                            }
//                        }
//                    }
//                    simulator.clearHints();
//                }
//            }
//        }
//    }
}
