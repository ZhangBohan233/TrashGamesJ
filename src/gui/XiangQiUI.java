package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import program.Chess;
import program.ChessGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class XiangQiUI implements Initializable {

    private final static int BLOCK_SIZE = 64;

    private final static int SELECT = 4;

    private final static int DESELECT = 5;

    private final static int MOVE = 6;

    @FXML
    private Canvas canvas;

    @FXML
    private Canvas redDeadCanvas, blackDeadCanvas;

    private ChessGame chessGame;

    private Paint boardPaint, redChessPaint, blackChessPaint,
            redTextPaint, blackTextPaint, chessSurfacePaint, selectionSurfacePaint,
            hintPaint;

    private Chess selection;

    InputStream inputStream;

    OutputStream outputStream;

    private ResourceBundle resources;

    boolean isServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        boardPaint = Paint.valueOf("black");
        redChessPaint = Paint.valueOf("red");
        blackChessPaint = Paint.valueOf("black");
        redTextPaint = Paint.valueOf("red");
        blackTextPaint = Paint.valueOf("black");

        hintPaint = Paint.valueOf("gray");

        selectionSurfacePaint = Paint.valueOf("cyan");
        chessSurfacePaint = Paint.valueOf("white");

        startGame();
        draw();

        setOnClickHandler();
    }

    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    public void listen() {
        Thread thread = new Thread(() -> {
            try {
                byte[] buffer = new byte[3];
                int action;
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    action = buffer[0] & 0xff;
                    final int r = buffer[1] & 0xff;
                    final int c = buffer[2] & 0xff;
                    if (action == SELECT) {
                        Platform.runLater(() -> {
                            chessGame.selectPosition(r, c);
                            selection = chessGame.getChessAt(r, c);
                            draw();
                        });
                    } else if (action == DESELECT) {
                        Platform.runLater(() -> {
                            chessGame.deSelectPosition(r, c);
                            selection = null;
                            draw();
                        });
                    } else if (action == MOVE) {
                        Platform.runLater(() -> {
                            chessGame.move(r, c);
                            selection = null;
                            draw();
                            drawDead();
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawBoard(gc);
        drawChess(gc);
    }

    public void startGame() {
        chessGame = new ChessGame();
        chessGame.initialize();
    }

    private void drawBoard(GraphicsContext gc) {
        gc.setStroke(boardPaint);
        gc.setLineWidth(2.0);
        int i;
        double x, y;
        for (i = 0; i < 10; i++) {
            y = getScreenY(i);
            gc.strokeLine(BLOCK_SIZE, y, 9 * BLOCK_SIZE, y);
        }
        for (i = 0; i < 9; i++) {
            x = getScreenX(i);
            gc.strokeLine(x, BLOCK_SIZE, x, 5 * BLOCK_SIZE);
            gc.strokeLine(x, 6 * BLOCK_SIZE, x, 10 * BLOCK_SIZE);
        }
        // draw connection
        gc.strokeLine(BLOCK_SIZE, 5 * BLOCK_SIZE,
                BLOCK_SIZE, 6 * BLOCK_SIZE);
        gc.strokeLine(9 * BLOCK_SIZE, 5 * BLOCK_SIZE,
                9 * BLOCK_SIZE, 6 * BLOCK_SIZE);

        // draw crosses
        gc.strokeLine(getScreenX(3), getScreenY(0),
                getScreenX(5), getScreenY(2));
        gc.strokeLine(getScreenX(3), getScreenY(7),
                getScreenX(5), getScreenY(9));
        gc.strokeLine(getScreenX(5), getScreenY(0),
                getScreenX(3), getScreenY(2));
        gc.strokeLine(getScreenX(5), getScreenY(7),
                getScreenX(3), getScreenY(9));

    }

    private void drawChess(GraphicsContext gc) {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                Chess chess = chessGame.getChessAt(row, col);
                if (chess != null) {
                    double x = getScreenX(col);
                    double y = getScreenY(row);
                    drawOneChess(gc, chess, x, y);
                }
                if (chessGame.getHintAt(row, col)) {
                    gc.setFill(hintPaint);
                    double x = getScreenX(col);
                    double y = getScreenY(row);
                    gc.fillOval(x - 6, y - 6, 12, 12);
                }
            }
        }
    }

    private void drawOneChess(GraphicsContext gc, Chess chess, double x, double y) {
        Paint paint, textPaint, surfacePaint;
        if (chess.isRed()) {
            paint = redChessPaint;
            textPaint = redTextPaint;
        } else {
            paint = blackChessPaint;
            textPaint = blackTextPaint;
        }
        if (chess.isSelected()) {
            surfacePaint = selectionSurfacePaint;
        } else {
            surfacePaint = chessSurfacePaint;
        }

        gc.setStroke(paint);
        gc.strokeOval(x - 24, y - 24, 48, 48);

        gc.setFill(surfacePaint);
        gc.fillOval(x - 22, y - 22, 44, 44);

        gc.setFill(textPaint);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(new Font(24));
        gc.fillText(chess.toString(), x, y + 6);
    }

    private void drawDead() {
        GraphicsContext rgc = redDeadCanvas.getGraphicsContext2D();
        GraphicsContext bgc = blackDeadCanvas.getGraphicsContext2D();

        rgc.clearRect(0, 0, redDeadCanvas.getWidth(), redDeadCanvas.getHeight());
        bgc.clearRect(0, 0, blackDeadCanvas.getWidth(), blackDeadCanvas.getHeight());

        for (int i = 0; i < chessGame.getBlackDeaths().size(); i++) {
            Chess dead = chessGame.getBlackDeaths().get(i);
            drawDeadChess(bgc, i, dead);
        }
        for (int i = 0; i < chessGame.getRedDeaths().size(); i++) {
            Chess dead = chessGame.getRedDeaths().get(i);
            drawDeadChess(rgc, i, dead);
        }
    }

    private void drawDeadChess(GraphicsContext gc, int count, Chess chess) {
        double x;
        double y;
        int minus = BLOCK_SIZE / 2;
        if (count < 9) {
            x = getScreenX(count);
            y = getScreenY(0) - minus;
        } else {
            x = getScreenX(count - 9);
            y = getScreenY(1) - minus;
        }
        drawOneChess(gc, chess, x, y);
    }

    private double getScreenX(int col) {
        return (col + 1) * BLOCK_SIZE;
    }

    private double getScreenY(int row) {
        return (row + 1) * BLOCK_SIZE;
    }

    private int[] getClicked(double x, double y) {
        int row = (int) Math.round((y - BLOCK_SIZE) / BLOCK_SIZE);
        int col = (int) Math.round((x - BLOCK_SIZE) / BLOCK_SIZE);

        return new int[]{row, col};
    }

    private void setOnClickHandler() {
        canvas.setOnMouseClicked(e -> {
            double x = e.getX();
            double y = e.getY();

            int[] pos = getClicked(x, y);
            if (isServer == chessGame.isRedTurn()) {
                if (checkPos(pos)) {
                    if (selection == null) {
                        boolean click = chessGame.selectPosition(pos[0], pos[1]);
                        if (click) {
                            selection = chessGame.getChessAt(pos[0], pos[1]);
                            send(SELECT, pos[0], pos[1]);
                            draw();
                        }
                    } else {
                        // move
                        Chess clicked = chessGame.getChessAt(pos[0], pos[1]);
                        if (clicked == selection) {
                            selection = null;
                            chessGame.deSelectPosition(pos[0], pos[1]);
                            send(DESELECT, pos[0], pos[1]);
                            draw();
                        } else {
                            if (chessGame.move(pos[0], pos[1])) {
                                // Successfully moved
                                selection = null;
                                send(MOVE, pos[0], pos[1]);
                                draw();
                                if (chessGame.isTerminated()) {
                                    String p;
                                    if (chessGame.isRedWin()) {
                                        p = resources.getString("red_win");
                                    } else {
                                        p = resources.getString("black_win");
                                    }
                                    showAlert(resources.getString("game_over"), p, "");
                                }
                                drawDead();
//                            redDead.invalidate();
//                            blackDead.invalidate();
                            }
                        }
                    }
                }
            }
        });

    }

    private void send(int action, int r, int c) {
        byte[] array = new byte[]{(byte) action, (byte) r, (byte) c};

        try {
            outputStream.write(array);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkPos(int[] pos) {
        return pos[0] >= 0 && pos[0] < 10 && pos[1] >= 0 && pos[1] < 9;
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }
}
