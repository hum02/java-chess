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

import static chess.domain.piece.move_rule.TestFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PawnMoveRuleTest {
    private Piece blackPiece;
    private Piece whitePiece;
    private final PawnMoveRule blackMoveRule = PawnMoveRule.from(PlayingCamp.BLACK);
    private final PawnMoveRule whiteMoveRule = PawnMoveRule.from(PlayingCamp.WHITE);
    private Map<Position, Piece> board;

    @BeforeAll
    void setUp() {
        blackPiece = new Piece(PawnMoveRule.from(PlayingCamp.BLACK), PlayingCamp.BLACK);
        whitePiece = new Piece(PawnMoveRule.from(PlayingCamp.WHITE), PlayingCamp.WHITE);
    }

    @BeforeEach
    void initBoard() {
        board = new HashMap<>();
    }

    @Test
    void 폰_대각선_움직임_실패_다른방향() {
        assertThatThrownBy(() -> blackMoveRule.move(B2, C3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 진영의 폰이 갈 수 없는 방향입니다.");
    }

    @Test
    void 폰_대각선_움직임_성공() {
        assertThat(blackMoveRule.move(B2, A1)).isEmpty();
    }

    @Test
    void 폰_움직임_실패_초기위치가_아님() {
        assertThatThrownBy(() -> blackMoveRule.move(A6, A8))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("폰이 갈 수 없는 위치입니다. 거리가 멉니다.");
    }

    @Test
    void 폰_움직임_성공_두칸이동() {
        assertThat(blackMoveRule.move(A7, A5)).containsExactly(A6);
    }
}
