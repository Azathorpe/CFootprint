package com.azathorpe.settings;

import com.intellij.util.messages.Topic;

public interface FontChangedNotifier {
    Topic<FontChangedNotifier> TOPIC =
            Topic.create("CFootprint font changed", FontChangedNotifier.class);

    void fontChanged();
}