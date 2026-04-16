package org.nowstart.zunyang.partypanic;

import com.badlogic.gdx.Game;
import org.nowstart.zunyang.partypanic.screen.PartyPanicScreen;

public final class PartyPanicGame extends Game {
    @Override
    public void create() {
        setScreen(new PartyPanicScreen());
    }
}
