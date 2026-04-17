package org.nowstart.zunyang.partypanic.adapter.out.save;

import java.util.Optional;
import org.nowstart.zunyang.partypanic.application.port.out.LoadFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.application.port.out.SaveFinaleStageStatePort;
import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

public final class InMemoryFinaleStageStateAdapter
    implements LoadFinaleStageStatePort, SaveFinaleStageStatePort {

    private FinaleStageState finaleStageState;

    @Override
    public Optional<FinaleStageState> load() {
        return Optional.ofNullable(finaleStageState);
    }

    @Override
    public void save(FinaleStageState finaleStageState) {
        this.finaleStageState = finaleStageState;
    }
}
