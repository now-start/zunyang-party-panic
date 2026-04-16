package org.nowstart.zunyang.partypanic.application.port;

import org.nowstart.zunyang.partypanic.domain.activity.ActivityId;

public interface GameNavigator {
    boolean showsOperationalUi();

    void showTitle();

    void showHub(String notice);

    void openActivity(ActivityId activityId);

    void completeScoredActivity(ActivityId activityId, int score);

    void completeStoryActivity(ActivityId activityId, String notice);
}
