package chess.dao;

import chess.domain.chessGame.ChessGame;
import chess.domain.chessGame.PlayingChessGame;
import chess.domain.chessGame.ReadyChessGame;
import chess.domain.piece.Piece;
import chess.domain.piece.PieceType;
import chess.domain.piece.PlayingCamp;
import chess.domain.position.File;
import chess.domain.position.Position;
import chess.domain.position.Rank;
import chess.dto.PieceDto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ChessGameDaoImpl implements ChessGameDao {

    private final Connection connection;

    public ChessGameDaoImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public ChessGame findChessGame() {
        PlayingCamp currentTurn = findTurn();
        if (currentTurn == null) {
            return new ReadyChessGame();
        }
        Map<Position, Piece> board = loadBoard();
        return new PlayingChessGame(board, currentTurn);
    }

    @Override
    public void updateChessGame(ChessGame gameState) {
        updateTurn(gameState.getThisTurn());
        deletePieces();
        savePieces(gameState.getPrintingBoard());
    }

    @Override
    public void deleteAll() {
        deleteTurn();
        deletePieces();
    }

    private PlayingCamp findTurn() {
        final var query = "SELECT * FROM chess_game";

        try (final var preparedStatement = connection.prepareStatement(query)) {
            final var resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return PlayingCamp.from(resultSet.getString("turn"));
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void updateTurn(PlayingCamp playingCamp) {
        deleteTurn();
        final var query = "insert into chess_game (turn) values (?)";

        try (final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, playingCamp.getName());

            preparedStatement.executeUpdate();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Map<Position, Piece> loadBoard() {
        final String query = "select * from piece";
        Map<Position, Piece> board = new HashMap<>();

        try (final var preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String x = resultSet.getString("x");
                String y = resultSet.getString("y");
                Position position = Position.of(File.of(x), Rank.of(y));

                String color = resultSet.getString("color");
                String type = resultSet.getString("type");
                Piece piece = new Piece(PieceType.from(type), PlayingCamp.from(color));

                board.put(position, piece);
            }
            return board;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void savePieces(Map<Position, PieceDto> pieces) {
        final var query = "insert into piece (x,y,color,type) values (?,?,?,?)";

        try (final var preparedStatement = connection.prepareStatement(query)) {
            for (Map.Entry<Position, PieceDto> entry : pieces.entrySet()) {
                preparedStatement.setString(1, entry.getKey().getFileIndex());
                preparedStatement.setString(2, entry.getKey().getRankIndex());
                preparedStatement.setString(3, entry.getValue().getColor().getName());
                preparedStatement.setString(4, entry.getValue().getPieceType().getName());

                preparedStatement.executeUpdate();
            }

        } catch (final Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void deleteTurn() {
        final String query = "delete from chess_game";
        try (final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void deletePieces() {
        final String query = "delete from piece";
        try (final var preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
