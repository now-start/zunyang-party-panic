package org.nowstart.zunyang.partypanic.adapter.in.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.IntSet;
import org.nowstart.zunyang.partypanic.domain.model.Direction;

public final class HubInputAdapter extends InputAdapter {
    private final IntSet pressedKeys = new IntSet();
    private boolean confirmRequested;
    private boolean backRequested;

    @Override
    public boolean keyDown(int keycode) {
        if (!pressedKeys.add(keycode)) {
            return true;
        }

        if (isConfirmKey(keycode)) {
            confirmRequested = true;
            return true;
        }
        if (isBackKey(keycode)) {
            backRequested = true;
            return true;
        }
        return isDirectionalKey(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return pressedKeys.remove(keycode);
    }

    public boolean consumeConfirmRequested() {
        boolean requested = confirmRequested;
        confirmRequested = false;
        return requested;
    }

    public boolean consumeBackRequested() {
        boolean requested = backRequested;
        backRequested = false;
        return requested;
    }

    public Direction pressedDirection() {
        if (pressedKeys.contains(Input.Keys.LEFT) || pressedKeys.contains(Input.Keys.A)) {
            return Direction.LEFT;
        }
        if (pressedKeys.contains(Input.Keys.RIGHT) || pressedKeys.contains(Input.Keys.D)) {
            return Direction.RIGHT;
        }
        if (pressedKeys.contains(Input.Keys.UP) || pressedKeys.contains(Input.Keys.W)) {
            return Direction.UP;
        }
        if (pressedKeys.contains(Input.Keys.DOWN) || pressedKeys.contains(Input.Keys.S)) {
            return Direction.DOWN;
        }
        return null;
    }

    private boolean isConfirmKey(int keycode) {
        return keycode == Input.Keys.ENTER
                || keycode == Input.Keys.SPACE
                || keycode == Input.Keys.E;
    }

    private boolean isBackKey(int keycode) {
        return keycode == Input.Keys.ESCAPE;
    }

    private boolean isDirectionalKey(int keycode) {
        return keycode == Input.Keys.LEFT
                || keycode == Input.Keys.RIGHT
                || keycode == Input.Keys.UP
                || keycode == Input.Keys.DOWN
                || keycode == Input.Keys.A
                || keycode == Input.Keys.D
                || keycode == Input.Keys.W
                || keycode == Input.Keys.S;
    }
}
