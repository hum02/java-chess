package chess.controller.command;

import chess.domain.chessGame.ChessGameState;

import java.util.Arrays;
import java.util.function.Function;

public enum Command {
    START("start", StartCommandExecute::new),
    MOVE("move", MoveCommandExecute::new),
    END("end", EndCommandExecute::new),
    STATUS("status", StatusCommandExecute::new);

    private final String command;
    private final Function<ChessGameState, CommandExecute> executorGenerator;

    Command(String command, Function<ChessGameState, CommandExecute> executorGenerator) {
        this.command = command;
        this.executorGenerator = executorGenerator;
    }

    public static Command findCommand(String commandType) {
        return Arrays.stream(Command.values())
                .filter(c -> c.command.equals(commandType))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("올바른 명령어가 아닙니다."));
    }

    public CommandExecute generateExecutor(ChessGameState chessGameState) {
        return this.executorGenerator.apply(chessGameState);
    }
}
