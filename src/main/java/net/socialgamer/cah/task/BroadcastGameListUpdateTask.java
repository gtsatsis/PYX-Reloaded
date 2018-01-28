package net.socialgamer.cah.task;

import net.socialgamer.cah.Constants.LongPollEvent;
import net.socialgamer.cah.EventWrapper;
import net.socialgamer.cah.data.ConnectedUsers;
import net.socialgamer.cah.data.QueuedMessage.MessageType;

public class BroadcastGameListUpdateTask extends SafeTimerTask {
    private final ConnectedUsers users;
    private volatile boolean needsUpdate = false;

    public BroadcastGameListUpdateTask(final ConnectedUsers users) {
        this.users = users;
    }

    public void needsUpdate() {
        needsUpdate = true;
    }

    @Override
    public void process() {
        if (needsUpdate) {
            users.broadcastToAll(MessageType.GAME_EVENT, new EventWrapper(LongPollEvent.GAME_LIST_REFRESH));
            needsUpdate = false;
        }
    }
}
