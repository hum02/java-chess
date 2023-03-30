package chess.domain.chessGame;

import chess.domain.Board;
import chess.domain.piece.PlayingCamp;
import chess.domain.piece.Piece;
import chess.domain.piece.move_rule.KingMoveRule;
import chess.domain.piece.move_rule.QueenMoveRule;
import chess.domain.position.Position;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static chess.domain.piece.move_rule.TestFixture.A1;
import static chess.domain.piece.move_rule.TestFixture.B1;
import static org.assertj.core.api.Assertions.assertThat;

class PlayingChessGameTest {
    static Board board;

    @BeforeAll
    static void beforeAll_setup() {
        Map<Position, Piece> piecePositions = new HashMap<>();
        piecePositions.put(B1, new Piece(KingMoveRule.getInstance(), PlayingCamp.BLACK));
        piecePositions.put(A1, new Piece(QueenMoveRule.getInstance(), PlayingCamp.WHITE));
        board = new Board(piecePositions);
    }

    @Test
    void 킹이_잡히면_게임이_종료된다() {
        ChessGame game = new PlayingChessGame(board);
        game.move("a1", "b1");

        assertThat(game.isEnd()).isTrue();
    }
}
