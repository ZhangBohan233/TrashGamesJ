package program;

import java.util.ArrayList;

public class ChessGame extends ChessGameTemplate {

    private ArrayList<Chess> blackDeaths = new ArrayList<>();
    private ArrayList<Chess> redDeaths = new ArrayList<>();

    private boolean terminated;
    private boolean redWins;

    public void initialize() {
        int[] baseOrder = new int[]{Chess.JV, Chess.MA, Chess.XIANG, Chess.SHI, Chess.JIANG,
                Chess.SHI, Chess.XIANG, Chess.MA, Chess.JV};
        for (int i = 0; i < 9; i++) {
            chessboard[0][i] = new Chess(baseOrder[i], false);
            chessboard[9][i] = new Chess(baseOrder[i], true);
        }
        chessboard[2][1] = new Chess(Chess.PAO, false);
        chessboard[2][7] = new Chess(Chess.PAO, false);
        chessboard[7][1] = new Chess(Chess.PAO, true);
        chessboard[7][7] = new Chess(Chess.PAO, true);
        for (int i = 0; i < 9; i += 2) {
            chessboard[3][i] = new Chess(Chess.ZU, false);
            chessboard[6][i] = new Chess(Chess.ZU, true);
        }
    }

    public ArrayList<Chess> getRedDeaths() {
        return redDeaths;
    }

    public ArrayList<Chess> getBlackDeaths() {
        return blackDeaths;
    }

    public boolean move(int[] destPos) {
        return move(destPos[0], destPos[1]);
    }

    public boolean move(int row, int col) {
        if (hintsBoard[row][col]) {
            Chess atPos = chessboard[row][col];
            if (atPos != null) {
                if (atPos.isRed()) {
                    if (atPos.getRep() == Chess.JIANG) {
                        terminated = true;
                        redWins = false;
                    } else {
                        redDeaths.add(atPos);
                    }
                } else {
                    if (atPos.getRep() == Chess.JIANG) {
                        terminated = true;
                        redWins = true;
                    } else {
                        blackDeaths.add(atPos);
                    }
                }
            }
            Chess moveChess = chessboard[selectR][selectC];
            moveChess.deSelect();
            chessboard[row][col] = moveChess;
            chessboard[selectR][selectC] = null;
            selectR = -1;
            selectC = -1;
            redTurn = !redTurn;
            clearHints();

            return true;
        }
        return false;
    }


    public boolean isTerminated() {
        return terminated;
    }

    public boolean isRedWin() {
        return redWins;
    }

    public boolean isRedTurn() {
        return redTurn;
    }

    public void terminate() {
        terminated = true;
    }
}
