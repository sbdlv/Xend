package me.sergiobarriodelavega.xend.listeners;

import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatRecyclerListener extends RecyclerView.OnScrollListener {
    private boolean scrollToBottom;

    public boolean scrollToBottom() {
        return scrollToBottom;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if (!recyclerView.canScrollVertically(1) ) {
            scrollToBottom = true;
        } else {
            scrollToBottom = false;
        }
    }
}
