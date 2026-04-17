package org.nowstart.zunyang.partypanic.application.port.out;

import org.nowstart.zunyang.partypanic.domain.common.GridActivityLayout;

public interface LoadGridActivityLayoutPort<T extends Enum<T>> {

    GridActivityLayout<T> load();
}
