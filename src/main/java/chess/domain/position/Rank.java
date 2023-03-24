package chess.domain.position;

import java.util.Arrays;

public enum Rank {
    RANK1(0, '1'),
    RANK2(1, '2'),
    RANK3(2, '3'),
    RANK4(3, '4'),
    RANK5(4, '5'),
    RANK6(5, '6'),
    RANK7(6, '7'),
    RANK8(7, '8');

    public static final int MIN_RANK = 0;
    public static final int MAX_RANK = 7;
    public static final int ONE_SQUARE = 1;
    private final int index;
    private final char symbol;

    Rank(final int index, char symbol) {
        this.index = index;
        this.symbol = symbol;
    }

    public static Rank of(int number) {
        return Arrays.stream(values())
                .filter(rank -> rank.index == number)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Rank 위치 입니다."));
    }

    public static Rank of(char symbol) {
        return Arrays.stream(values())
                .filter(rank -> rank.symbol == symbol)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Rank 위치 입니다."));
    }

    public Rank move(int distance) {
        if (this.index + distance < MIN_RANK) {
            return Rank.of(MIN_RANK);
        }
        if (this.index + distance > MAX_RANK) {
            return Rank.of(MAX_RANK);
        }
        return Rank.of(this.index + distance);
    }

    public int distance(Rank other) {
        return Math.abs(this.index - other.index);
    }

    public Rank moveOnceToOther(Rank other) {
        if (other.index > this.index) {
            return next();
        }
        if (other.index < this.index) {
            return prev();
        }
        return this;
    }

    public int getRankIndex() {
        return MAX_RANK - index;
    }

    private Rank prev() {
        return Rank.of(this.index - ONE_SQUARE);
    }

    private Rank next() {
        return Rank.of(this.index + ONE_SQUARE);
    }

    @Override
    public String toString() {
        return "Rank{" +
                "number=" + index +
                "}";
    }
}
