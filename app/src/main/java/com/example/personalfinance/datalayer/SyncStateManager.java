package com.example.personalfinance.datalayer;

import com.example.personalfinance.datalayer.local.enums.SyncState;

public class SyncStateManager {
    public enum Action{
        delete, insert, update
    }

    public static SyncState determineSyncState(Action action, SyncState current_syncState){
        if ((action == Action.update && current_syncState == SyncState.not_insert_sync) ||
                (action == Action.update && current_syncState == SyncState.not_update_sync))
            return current_syncState;
        else if ((action == Action.delete && current_syncState == SyncState.not_insert_sync) ||
                (action == Action.delete && current_syncState == SyncState.not_update_sync))
            return SyncState.delete_no_sync;
        else if (action == Action.update && current_syncState == SyncState.synced)
            return SyncState.not_update_sync;
        else if (action == Action.delete && current_syncState == SyncState.synced)
            return SyncState.not_delete_sync;
        else
            return SyncState.not_insert_sync;
    }
}
