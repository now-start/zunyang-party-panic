package org.nowstart.zunyang.partypanic.application.signal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.nowstart.zunyang.partypanic.application.dto.command.AdjustSignalSettingCommand;
import org.nowstart.zunyang.partypanic.application.dto.command.MoveSignalActorCommand;
import org.nowstart.zunyang.partypanic.application.dto.result.SignalConsoleViewResult;
import org.nowstart.zunyang.partypanic.application.port.in.InspectSignalControlUseCase;
import org.nowstart.zunyang.partypanic.application.port.out.LoadSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveSignalConsoleStatePort;
import org.nowstart.zunyang.partypanic.domain.common.Direction;
import org.nowstart.zunyang.partypanic.domain.signal.SignalConsoleState;
import org.nowstart.zunyang.partypanic.support.ActivityTestLayouts;

class SignalConsoleInteractorTest {

    @Test
    void start_seeds_initial_console_state() {
        InMemorySignalConsolePort statePort = new InMemorySignalConsolePort();
        StartSignalConsoleInteractor interactor = new StartSignalConsoleInteractor(
            ActivityTestLayouts::signalConsole,
            statePort
        );

        SignalConsoleViewResult result = interactor.start();

        assertEquals("첫 신호 맞추기", result.title());
        assertEquals(4, result.controls().size());
        assertEquals(2, result.actorX());
        assertEquals(2, result.actorY());
        assertEquals("MIC", result.controls().getFirst().id());
    }

    @Test
    void movement_and_adjustment_flow_reaches_stabilized_state() {
        InMemorySignalConsolePort statePort = new InMemorySignalConsolePort();
        StartSignalConsoleInteractor startInteractor = new StartSignalConsoleInteractor(
            ActivityTestLayouts::signalConsole,
            statePort
        );
        MoveSignalActorInteractor moveInteractor = new MoveSignalActorInteractor(statePort, statePort);
        InspectSignalControlUseCase inspectInteractor = new InspectSignalControlInteractor(statePort, statePort);
        AdjustSignalSettingInteractor adjustInteractor = new AdjustSignalSettingInteractor(statePort, statePort);

        startInteractor.start();
        moveInteractor.move(new MoveSignalActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveSignalActorCommand(Direction.UP));
        inspectInteractor.inspect();
        adjustInteractor.adjust(new AdjustSignalSettingCommand(1));
        moveInteractor.move(new MoveSignalActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveSignalActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveSignalActorCommand(Direction.UP));
        inspectInteractor.inspect();
        adjustInteractor.adjust(new AdjustSignalSettingCommand(-1));
        moveInteractor.move(new MoveSignalActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveSignalActorCommand(Direction.LEFT));
        moveInteractor.move(new MoveSignalActorCommand(Direction.DOWN));
        inspectInteractor.inspect();
        adjustInteractor.adjust(new AdjustSignalSettingCommand(1));
        moveInteractor.move(new MoveSignalActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveSignalActorCommand(Direction.RIGHT));
        moveInteractor.move(new MoveSignalActorCommand(Direction.DOWN));
        inspectInteractor.inspect();
        SignalConsoleViewResult result = adjustInteractor.adjust(new AdjustSignalSettingCommand(-1));

        assertTrue(result.stabilized());
        assertEquals("CUE", result.activeControlId());
        assertEquals("정시", result.controls().get(3).currentDescriptor());
    }

    private static final class InMemorySignalConsolePort
        implements LoadSignalConsoleStatePort, SaveSignalConsoleStatePort {

        private SignalConsoleState signalConsoleState;

        @Override
        public Optional<SignalConsoleState> load() {
            return Optional.ofNullable(signalConsoleState);
        }

        @Override
        public void save(SignalConsoleState signalConsoleState) {
            this.signalConsoleState = signalConsoleState;
        }
    }
}
