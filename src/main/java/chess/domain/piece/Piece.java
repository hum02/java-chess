package chess.domain.piece;

import chess.domain.piece.move_rule.MoveRule;
import chess.domain.position.Position;

import java.util.List;
import java.util.Objects;

public class Piece {
    private final MoveRule moveRule;
    private final Color color;

    public Piece(MoveRule moveRule, Color color) {
        this.moveRule = moveRule;
        this.color = color;
    }

    public List<Position> move(Position currentPosition, Position nextPosition) {
        return moveRule.move(currentPosition, nextPosition);
    }

    public boolean isOpponent(Piece other) {
        return this.color != other.color;
    }

    public boolean isFriendly(Piece other) {
        return this.color == other.color;
    }

    public boolean isSameColor(Color color) {
        return this.color == color;
    }

    public boolean isPawn() {
        return moveRule.isPawnMove();
    }

    public PieceType getPieceType() {
        return moveRule.pieceType();
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return Objects.equals(moveRule.getClass(), piece.moveRule.getClass()) && color == piece.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moveRule.getClass(), color);
    }

    @Override
    public String toString() {
        return "Piece{" +
                "moveRule=" + moveRule +
                ", color=" + color +
                '}';
    }
}
