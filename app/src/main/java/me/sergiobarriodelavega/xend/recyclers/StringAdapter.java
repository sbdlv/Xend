package me.sergiobarriodelavega.xend.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.sergiobarriodelavega.xend.R;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {
    private List<String> values;
    private View.OnClickListener onClickListener;

    public StringAdapter(List<String> values) {
        this.values = values;
    }

    public StringAdapter(List<String> values, View.OnClickListener onClickListener) {
        this.values = values;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_string, parent, false);
        view.setOnClickListener(onClickListener);
        return new StringAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvString.setText(values.get(position));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvString;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvString = view.findViewById(R.id.tvStringText);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
