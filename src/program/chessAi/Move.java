package program.chessAi;

import java.util.Arrays;

public class Move {

    private int[] srcPos;
    private int[] destPos;

    Move(int[] srcPos, int[] destPos) {
        this.srcPos = srcPos;
        this.destPos = destPos;
    }

    @Override
    public String toString() {
        return Arrays.toString(srcPos) + "->" + Arrays.toString(destPos);
    }

    @Override
    public int hashCode() {
        return (srcPos[0] << 12) | (srcPos[1] << 8) | (destPos[0] << 4) | destPos[1];
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Move && Arrays.equals(srcPos, destPos);
    }

    public int[] getDestPos() {
        return destPos;
    }

    public int[] getSrcPos() {
        return srcPos;
    }
}
