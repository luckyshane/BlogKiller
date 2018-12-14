package com.github.luckyshane.blogkiller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.luckyshane.blogkiller.R;
import com.github.luckyshane.blogkiller.SimpleReadActivity;
import com.github.luckyshane.blogkiller.bean.BlogItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlogItemAdapter extends RecyclerView.Adapter {
    private List<BlogItem> blogItems;
    private Context context;
    private OnItemEditClickListener onItemEditClickListener;

    public void setOnItemEditClickListener(OnItemEditClickListener onItemEditClickListener) {
        this.onItemEditClickListener = onItemEditClickListener;
    }

    public void setOnItemDeleteClickListener(OnItemDeleteClickListener onItemDeleteClickListener) {
        this.onItemDeleteClickListener = onItemDeleteClickListener;
    }

    private OnItemDeleteClickListener onItemDeleteClickListener;

    public interface OnItemEditClickListener {
        void onClick(BlogItem item);
    }
    public interface OnItemDeleteClickListener {
        void onDelete(BlogItem item);
    }

    public BlogItemAdapter(Context context, List<BlogItem> blogItems) {
        this.blogItems = blogItems;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blog, viewGroup, false);
        return new BlogViewHolder(rootView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        BlogViewHolder blogViewHolder = (BlogViewHolder) viewHolder;
        final BlogItem item = blogItems.get(i);
        blogViewHolder.urlTv.setText(item.url);
        blogViewHolder.countTv.setText(String.format("count: %d", item.count));
        blogViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleReadActivity.openThis(context, item.url, item.count);
            }
        });
        blogViewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEditClickListener != null) {
                    onItemEditClickListener.onClick(item);
                }
            }
        });
        blogViewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemDeleteClickListener != null) {
                    onItemDeleteClickListener.onDelete(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogItems != null ? blogItems.size() : 0;
    }

    public void update(List<BlogItem> blogItems) {
        this.blogItems = blogItems;
        notifyDataSetChanged();
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.edit_btn)
        View editBtn;
        @BindView(R.id.delete_btn)
        View deleteBtn;
        @BindView(R.id.url_tv)
        TextView urlTv;
        @BindView(R.id.count_tv)
        TextView countTv;


        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }





}
