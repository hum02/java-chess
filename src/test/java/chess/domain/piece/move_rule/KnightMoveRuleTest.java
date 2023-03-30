package chess.domain.piece.move_rule;

import chess.domain.piece.PlayingCamp;
import chess.domain.piece.Piece;
import chess.domain.position.Position;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.HashMap;
import java.util.Map;

import static chess.domain.piece.move_rule.TestFixture.A1;
import static chess.domain.piece.move_rule.TestFixture.B2;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KnightMoveRuleTest {
    private Piece blackPiece;
    private Piece whitePiece;
    private final MoveRule moveRule = KnightMoveRule.getInstance();
    private Map<Position, Piece> board;

    @BeforeAll
    void setUp() {
        blackPiece = new Piece(moveRule, PlayingCamp.BLACK);
        whitePiece = new Piece(moveRule, PlayingCamp.WHITE);
    }

    @BeforeEach
    void initBoard() {
        board = new HashMap<>();
    }

    @Test
    void 나이트_움직임_실패() {
        board.put(A1, blackPiece);
        board.put(B2, whitePiece);

        assertThatThrownBy(() -> moveRule.move(A1, B2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("나이트는 L 모양으로만 움직일 수 있습니다.");
    }
}
