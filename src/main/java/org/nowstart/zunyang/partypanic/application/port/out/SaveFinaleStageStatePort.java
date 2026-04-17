package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.finale.FinaleStageState;

public interface SaveFinaleStageStatePort {

    void save(FinaleStageState finaleStageState);
}
