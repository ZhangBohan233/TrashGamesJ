package program;

import java.util.ArrayList;

public class ChessGame {

    private Chess[][] chessboard = new Chess[10][9];
    private boolean[][] hintsBoard = new boolean[10][9];

    private ArrayList<Chess> blackDeaths = new ArrayList<>();
    private ArrayList<Chess> redDeaths = new ArrayList<>();

    private boolean redTurn = true;

    private int selectR = -1;
    private int selectC = -1;

    private boolean terminated;
    private boolean redWins;

    public ChessGame() {

    }

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

    public Chess getChessAt(int row, int col) {
        return chessboard[row][col];
    }

    public boolean getHintAt(int row, int col) {
        return hintsBoard[row][col];
    }

    public boolean selectPosition(int row, int col) {
        Chess clicked = chessboard[row][col];
        if (clicked != null) {
            if (clicked.isRed() ^ (!redTurn)) {
                clicked.select();
                selectR = row;
                selectC = col;
                fillHints(row, col);
                return true;
            }
        }
        return false;
    }

    public void deSelectPosition(int row, int col) {
        selectR = -1;
        selectC = -1;
        chessboard[row][col].deSelect();
        clearHints();
    }

    public Chess getSelected() {
        if (selectR != -1 && selectC != -1) {
            return chessboard[selectR][selectC];
        } else {
            return null;
        }
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

    private void fillHints(int row, int col) {
        Chess chess = chessboard[row][col];

        switch (chess.getRep()) {
            case Chess.JIANG:
                showJiang(chess, row, col);
                break;

            case Chess.SHI:
                showShi(chess, row, col);
                break;

            case Chess.XIANG:
                showXiang(chess, row, col);
                break;

            case Chess.MA:
                showMa(chess, row, col);
                break;

            case Chess.JV:
                showJv(chess, row, col);
                break;

            case Chess.PAO:
                showPao(chess, row, col);
                break;

            case Chess.ZU:
                showZu(chess, row, col);
                break;

            default:
                break;
        }
    }

    private void markHint(Chess chess, int r, int c) {
        Chess posChess = chessboard[r][c];
        if (posChess != null) {
            if (posChess.isRed() == chess.isRed()) {
                return;
            }
        }
        hintsBoard[r][c] = true;
    }

    private void clearHints() {
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 9; c++) {
                hintsBoard[r][c] = false;
            }
        }
    }

    private void showJv(Chess chess, int row, int col) {
        for (int r = row + 1; r < 10; r++) {
            Chess posChess = chessboard[r][col];
            if (posChess == null) {
                hintsBoard[r][col] = true;
            } else {
                if (posChess.isRed() != chess.isRed()) {
                    hintsBoard[r][col] = true;
                }
                break;
            }
        }
        for (int r = row - 1; r >= 0; r--) {
            Chess posChess = chessboard[r][col];
            if (posChess == null) {
                hintsBoard[r][col] = true;
            } else {
                if (posChess.isRed() != chess.isRed()) {
                    hintsBoard[r][col] = true;
                }
                break;
            }
        }
        for (int c = col + 1; c < 9; c++) {
            Chess posChess = chessboard[row][c];
            if (posChess == null) {
                hintsBoard[row][c] = true;
            } else {
                if (posChess.isRed() != chess.isRed()) {
                    hintsBoard[row][c] = true;
                }
                break;
            }
        }
        for (int c = col - 1; c >= 0; c--) {
            Chess posChess = chessboard[row][c];
            if (posChess == null) {
                hintsBoard[row][c] = true;
            } else {
                if (posChess.isRed() != chess.isRed()) {
                    hintsBoard[row][c] = true;
                }
                break;
            }
        }
    }

    private void showPao(Chess chess, int row, int col) {
        boolean direct = true;
        for (int r = row + 1; r < 10; r++) {
            Chess posChess = chessboard[r][col];
            if (posChess == null) {
                if (direct) {
                    hintsBoard[r][col] = true;
                }
            } else {
                if (direct) {
                    direct = false;
                } else {
                    if (posChess.isRed() != chess.isRed()) {
                        hintsBoard[r][col] = true;
                    }
                    break;
                }
            }
        }
        direct = true;
        for (int r = row - 1; r >= 0; r--) {
            Chess posChess = chessboard[r][col];
            if (posChess == null) {
                if (direct) {
                    hintsBoard[r][col] = true;
                }
            } else {
                if (direct) {
                    direct = false;
                } else {
                    if (posChess.isRed() != chess.isRed()) {
                        hintsBoard[r][col] = true;
                    }
                    break;
                }
            }
        }

        direct = true;
        for (int c = col + 1; c < 9; c++) {
            Chess posChess = chessboard[row][c];
            if (posChess == null) {
                if (direct) {
                    hintsBoard[row][c] = true;
                }
            } else {
                if (direct) {
                    direct = false;
                } else {
                    if (posChess.isRed() != chess.isRed()) {
                        hintsBoard[row][c] = true;
                    }
                    break;
                }
            }
        }
        direct = true;
        for (int c = col - 1; c >= 0; c--) {
            Chess posChess = chessboard[row][c];
            if (posChess == null) {
                if (direct) {
                    hintsBoard[row][c] = true;
                }
            } else {
                if (direct) {
                    direct = false;
                } else {
                    if (posChess.isRed() != chess.isRed()) {
                        hintsBoard[row][c] = true;
                    }
                    break;
                }
            }
        }
    }

    private void showMa(Chess chess, int row, int col) {
        int r, c, r2, c2;

        // right down 2
        r = row + 2;
        c = col + 1;
        r2 = row + 1;
        c2 = col;
        showMaInternal(chess, r, c, r2, c2);

        // right down 1
        r = row + 1;
        c = col + 2;
        r2 = row;
        c2 = col + 1;
        showMaInternal(chess, r, c, r2, c2);

        // right up 1
        r = row - 1;
        c = col + 2;
        r2 = row;
        c2 = col + 1;
        showMaInternal(chess, r, c, r2, c2);

        // right up 2
        r = row - 2;
        c = col + 1;
        r2 = row - 1;
        c2 = col;
        showMaInternal(chess, r, c, r2, c2);

        // left up 2
        r = row - 2;
        c = col - 1;
        r2 = row - 1;
        c2 = col;
        showMaInternal(chess, r, c, r2, c2);

        // left up 1
        r = row - 1;
        c = col - 2;
        r2 = row;
        c2 = col - 1;
        showMaInternal(chess, r, c, r2, c2);

        // left down 1
        r = row + 1;
        c = col - 2;
        r2 = row;
        c2 = col - 1;
        showMaInternal(chess, r, c, r2, c2);

        // left down 2
        r = row + 2;
        c = col - 1;
        r2 = row + 1;
        c2 = col;
        showMaInternal(chess, r, c, r2, c2);

    }

    private void showMaInternal(Chess chess, int r, int c, int r2, int c2) {
        if (inBoard(r, c) && inBoard(r2, c2)) {
            Chess dest = chessboard[r][c];
            Chess path = chessboard[r2][c2];
            if (path == null) {
                if (dest == null || dest.isRed() != chess.isRed()) {
                    hintsBoard[r][c] = true;
                }
            }
        }
    }

    private void showXiang(Chess chess, int row, int col) {
        int[] rs = new int[]{1, 1, -1, -1};
        int[] cs = new int[]{1, -1, 1, -1};
        for (int i = 0; i < 4; i++) {
            showXiangInternal(chess, row + rs[i] * 2, col + cs[i] * 2,
                    row + rs[i], col + cs[i]);
        }
    }

    private void showXiangInternal(Chess chess, int r, int c, int r2, int c2) {
        if (inHalf(chess.isRed(), r, c) && inHalf(chess.isRed(), r2, c2)) {
            Chess dest = chessboard[r][c];
            Chess path = chessboard[r2][c2];
            if (path == null) {
                if (dest == null || dest.isRed() != chess.isRed()) {
                    hintsBoard[r][c] = true;
                }
            }
        }
    }

    private void showShi(Chess chess, int row, int col) {
        for (int r = row - 1; r < row + 2; r++) {
            for (int c = col - 1; c < col + 2; c++) {
                if (inHome(chess.isRed(), r, c) && r != row && c != col) {
                    markHint(chess, r, c);
                }
            }
        }
    }

    private void showJiang(Chess chess, int row, int col) {
        for (int r = row - 1; r < row + 2; r++) {
            for (int c = col - 1; c < col + 2; c++) {
                if ((r == row || c == col) && inHome(chess.isRed(), r, c)) {
                    markHint(chess, r, c);
                }
            }
        }
        if (chess.isRed()) {
            for (int r = row - 1; r >= 0; r--) {
                Chess posChess = chessboard[r][col];
                if (posChess != null) {
                    if (posChess.getRep() == Chess.JIANG) {
                        hintsBoard[r][col] = true;
                    }
                    break;
                }
            }
        } else {
            for (int r = row + 1; r < 10; r++) {
                Chess posChess = chessboard[r][col];
                if (posChess != null) {
                    if (posChess.getRep() == Chess.JIANG) {
                        hintsBoard[r][col] = true;
                    }
                    break;
                }
            }
        }
    }

    private void showZu(Chess chess, int row, int col) {
        if (chess.isRed()) {
            if (row > 4) {
                // At self side
                markHint(chess, row - 1, col);
            } else {
                if (row > 0) markHint(chess, row - 1, col);
                if (col > 0) markHint(chess, row, col - 1);
                if (col < 8) markHint(chess, row, col + 1);
            }
        } else {
            if (row < 5) {
                // At self side
                markHint(chess, row + 1, col);
            } else {
                if (row < 9) markHint(chess, row + 1, col);
                if (col > 0) markHint(chess, row, col - 1);
                if (col < 8) markHint(chess, row, col + 1);
            }
        }
    }

    private boolean inBoard(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 9;
    }

    private boolean inHome(boolean isRed, int row, int col) {
        if (col < 3 || col > 5) return false;
        else {
            if (isRed) {
                return row < 10 && row > 6;
            } else {
                return row >= 0 && row < 3;
            }
        }
    }

    private boolean inHalf(boolean isRed, int row, int col) {
        if (col < 0 || col > 8) {
            return false;
        } else {
            if (isRed) {
                return row > 4 && row < 10;
            } else {
                return row < 5 && row >= 0;
            }
        }
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
