package program;

public class Chess {

    public final static int JIANG = 0;
    public final static int SHI = 1;
    public final static int XIANG = 2;
    public final static int MA = 3;
    public final static int JV = 4;
    public final static int PAO = 5;
    public final static int ZU = 6;

    private final static String[] BLACK_CHESS =
            new String[]{"將", "士", "象", "馬", "車", "砲", "卒"};

    private final static String[] RED_CHESS =
            new String[]{"帥", "仕", "相", "傌", "俥", "炮", "兵"};

    /**
     * Red or Black. 0 for black, 1 for red.
     */
    private boolean red;

    private int rep;

    private boolean selected;

    public Chess(int rep, boolean isRed) {
        this.rep = rep;
        this.red = isRed;
    }

    public boolean isRed() {
        return red;
    }

    public void select() {
        selected = true;
    }

    public void deSelect() {
        selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public int getRep() {
        return rep;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Chess && ((Chess) obj).rep == rep && ((Chess) obj).isRed() == isRed();
    }

    @Override
    public String toString() {
        if (isRed()) {
            return RED_CHESS[rep];
        } else {
            return BLACK_CHESS[rep];
        }
    }
}
