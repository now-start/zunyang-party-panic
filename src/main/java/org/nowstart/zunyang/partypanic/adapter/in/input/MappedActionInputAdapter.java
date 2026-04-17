package org.nowstart.zunyang.partypanic.adapter.in.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;

import java.util.ArrayDeque;
import java.util.Map;

public final class MappedActionInputAdapter<A> extends InputAdapter {
    private final IntMap<A> keyBindings = new IntMap<>();
    private final IntSet pressedKeys = new IntSet();
    private final ArrayDeque<A> queuedActions = new ArrayDeque<>();

    public MappedActionInputAdapter(Map<Integer, A> keyBindings) {
        keyBindings.forEach(this.keyBindings::put);
    }

    @Override
    public boolean keyDown(int keycode) {
        A action = keyBindings.get(keycode);
        if (action == null) {
            return false;
        }

        if (!pressedKeys.add(keycode)) {
            return true;
        }

        queuedActions.addLast(action);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return pressedKeys.remove(keycode);
    }

    public A pollAction() {
        return queuedActions.pollFirst();
    }
}
