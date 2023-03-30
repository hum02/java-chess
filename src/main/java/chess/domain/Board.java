package chess.domain;

import chess.domain.piece.BlankPiece;
import chess.domain.piece.Piece;
import chess.domain.piece.PlayingCamp;
import chess.domain.position.File;
import chess.domain.position.Position;
import chess.dto.PieceDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Board {
    public static final int KING_DIE_SCORE = -1;
    public static final int DUPLICATE_EXIST = 2;

    private final Map<Position, Piece> board;
    private boolean isFinished;

    public Board(Map<Position, Piece> board) {
        this.board = board;
        this.isFinished = false;
    }

    public Map<Position, PieceDto> move(Position currentPosition, Position nextPosition, PlayingCamp thisTurn) {
        Piece currentPiece = board.getOrDefault(currentPosition, BlankPiece.getInstance());
        List<Position> routePositions = currentPiece.move(currentPosition, nextPosition);
        validateThisTurnColor(thisTurn, currentPiece);

        if (currentPiece.isPawn()) {
            return movePawn(currentPosition, nextPosition, routePositions);
        }
        return moveGeneralPiece(currentPosition, nextPosition, routePositions);
    }

    public List<Double> calculateScore() {
        double blackScore = getTeamScoreByColor(PlayingCamp.BLACK) - (0.5D * getVerticalSamePawnCount(PlayingCamp.BLACK));
        double whiteScore = getTeamScoreByColor(PlayingCamp.WHITE) - (0.5D * getVerticalSamePawnCount(PlayingCamp.WHITE));
        return List.of(blackScore, whiteScore);
    }

    public Map<Position, PieceDto> getPrintingBoard() {
        Map<Position, PieceDto> pieceDtos = new HashMap<>();
        for (Map.Entry<Position, Piece> entry : board.entrySet()) {
            Piece piece = entry.getValue();
            pieceDtos.put(entry.getKey(), new PieceDto(piece));
        }
        return pieceDtos;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    private void validateThisTurnColor(PlayingCamp thisTurn, Piece piece) {
        if (!piece.isSameCamp(thisTurn)) {
            throw new IllegalArgumentException("이번 차례에 움직일 수 있는 색의 기물이 아닙니다.");
        }
    }

    private Map<Position, PieceDto> moveGeneralPiece(Position currentPosition, Position nextPosition, List<Position> routePositions) {
        validateMiddlePathConflict(routePositions);
        validateDestinationConflict(currentPosition, nextPosition);

        updateMovedPiece(currentPosition, nextPosition);
        return getPrintingBoard();
    }

    private void validateDestinationConflict(Position currentPosition, Position nextPosition) {
        Piece currentPiece = board.getOrDefault(currentPosition, BlankPiece.getInstance());
        Piece nextPiece = board.getOrDefault(nextPosition, BlankPiece.getInstance());
        if (currentPiece.isFriendly(nextPiece)) {
            throw new IllegalArgumentException("이동 위치에 아군기물이 있어 이동할 수 없습니다.");
        }
    }

    private Map<Position, PieceDto> movePawn(Position currentPosition, Position nextPosition, List<Position> routePositions) {
        validateMiddlePathConflict(routePositions);
        if (!isFiancee(currentPosition, nextPosition) && !isForwardMovePossible(currentPosition, nextPosition)) {
            throw new IllegalArgumentException("해당위치에 이동할 수 없습니다. 폰은 적군 기물이 있어야 대각선 이동이, 다른 기물이 없어야 직선이동이 가능합니다.");
        }

        updateMovedPiece(currentPosition, nextPosition);
        return getPrintingBoard();
    }

    private void validateMiddlePathConflict(List<Position> routePositions) {
        if (routePositions.stream().anyMatch(board::containsKey)) {
            throw new IllegalArgumentException("이동경로 중간에 다른 기물이 있어 이동할 수 없습니다.");
        }
    }

    private boolean isFiancee(Position currentPosition, Position nextPosition) {
        Piece currentPiece = board.getOrDefault(currentPosition, BlankPiece.getInstance());
        Piece destinationPiece = board.getOrDefault(nextPosition, BlankPiece.getInstance());
        return currentPosition.isDiagonalEqual(nextPosition) && currentPiece.isOpponent(destinationPiece);
    }

    private boolean isForwardMovePossible(Position currentPosition, Position nextPosition) {
        return currentPosition.isStraightEqual(nextPosition) && !board.containsKey(nextPosition);
    }

    private void updateMovedPiece(Position currentPosition, Position nextPosition) {
        Piece movingPiece = board.getOrDefault(currentPosition, BlankPiece.getInstance());
        board.remove(currentPosition);
        Piece attacked = board.getOrDefault(nextPosition, BlankPiece.getInstance());
        board.put(nextPosition, movingPiece);
        if (attacked.isKing()) {
            this.isFinished = true;
        }
    }

    private double getTeamScoreByColor(PlayingCamp playingCamp) {
        List<Piece> pieces = board.values().stream()
                .filter(p -> p.isSameCamp(playingCamp))
                .collect(Collectors.toList());
        if (pieces.stream().noneMatch(Piece::isKing)) {
            return KING_DIE_SCORE;
        }
        return pieces.stream()
                .map(Piece::getScore)
                .reduce(0D, Double::sum);
    }

    private Long getVerticalSamePawnCount(PlayingCamp playingCamp) {
        long verticalPawnCount = 0;
        List<Position> pawnPositions = getPawnPositionsByColor(playingCamp);

        for (File file : File.values()) {
            long count = pawnPositions.stream()
                    .filter(p -> p.isSameFile(file))
                    .count();

            if (count >= DUPLICATE_EXIST) {
                verticalPawnCount += count;
            }
        }
        return verticalPawnCount;
    }

    private List<Position> getPawnPositionsByColor(PlayingCamp playingCamp) {
        return board.entrySet().stream()
                .filter(entry -> {
                    Piece piece = entry.getValue();
                    return piece.isPawn() && piece.isSameCamp(playingCamp);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
