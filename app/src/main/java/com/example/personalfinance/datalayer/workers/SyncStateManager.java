package com.example.personalfinance.datalayer.workers;

import android.util.Log;

import com.example.personalfinance.datalayer.local.enums.SyncState;

public class SyncStateManager {
    private static final String TAG = "kiev";
    public enum Action{
        delete, update, insert
    }

    public static SyncState determineSyncState(Action action, SyncState current_syncState){
        Log.d(TAG, "action: " + action + ", sync state: " + current_syncState);
        if (current_syncState == SyncState.not_sync_insert && action == Action.update)
            return current_syncState;
        else if (current_syncState == SyncState.not_sync_update && action == Action.update)
            return current_syncState;
        else if (current_syncState == SyncState.synced && action == Action.update)
            return SyncState.not_sync_update;
        return SyncState.not_sync_insert;
    }
}
