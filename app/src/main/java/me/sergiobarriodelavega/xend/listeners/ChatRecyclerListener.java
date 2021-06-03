package me.sergiobarriodelavega.xend.listeners;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatRecyclerListener extends RecyclerView.OnScrollListener {
    private boolean scrollToBottom;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton btnGoToBottom;

    public ChatRecyclerListener(LinearLayoutManager linearLayoutManager, FloatingActionButton btnGoToBottom) {
        this.linearLayoutManager = linearLayoutManager;
        this.btnGoToBottom = btnGoToBottom;
    }

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

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(linearLayoutManager.findLastVisibleItemPosition() != linearLayoutManager.getItemCount() - 1){
            btnGoToBottom.setVisibility(View.VISIBLE);
        } else {
            btnGoToBottom.setVisibility(View.INVISIBLE);
        }
    }
}
