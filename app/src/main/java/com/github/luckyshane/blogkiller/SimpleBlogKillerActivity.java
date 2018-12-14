package com.github.luckyshane.blogkiller;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.luckyshane.blogkiller.adapter.BlogItemAdapter;
import com.github.luckyshane.blogkiller.bean.BlogItem;
import com.github.luckyshane.blogkiller.db.BlogDao;
import com.luckyshane.github.sutil.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SimpleBlogKillerActivity extends AppCompatActivity {
    @BindView(R.id.blog_list_view)
    RecyclerView listView;
    private BlogItemAdapter adapter;
    private List<BlogItem> blogItemList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_blog_killer);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        blogItemList = new ArrayList<>(BlogDao.getAll());
        adapter = new BlogItemAdapter(this, blogItemList);
        adapter.setOnItemEditClickListener(new BlogItemAdapter.OnItemEditClickListener() {
            @Override
            public void onClick(final BlogItem blogItem) {
                final View contentView = LayoutInflater.from(SimpleBlogKillerActivity.this).inflate(R.layout.dialog_blog_edit, null);
                final EditText urlEdt = contentView.findViewById(R.id.url_edt);
                final EditText countEdit = contentView.findViewById(R.id.count_edit);
                urlEdt.setText(blogItem.url);
                countEdit.setText(blogItem.count + "");
                new AlertDialog.Builder(SimpleBlogKillerActivity.this)
                        .setView(contentView)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = urlEdt.getText().toString();
                                if (TextUtils.isEmpty(countEdit.getText().toString())) {
                                    Toast.makeText(SimpleBlogKillerActivity.this, "count cant be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                int count = Integer.parseInt(countEdit.getText().toString());
                                if (TextUtils.isEmpty(url)) {
                                    Toast.makeText(SimpleBlogKillerActivity.this, "url cannot be empty", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (count <= 0) {
                                    Toast.makeText(SimpleBlogKillerActivity.this, "count must > 0", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                dialog.cancel();
                                blogItem.url = url;
                                blogItem.count = count;
                                adapter.notifyDataSetChanged();
                                BlogDao.insertOrUpdateBlogItem(blogItem);
                            }
                        }).show();
            }
        });
        adapter.setOnItemDeleteClickListener(new BlogItemAdapter.OnItemDeleteClickListener() {
            @Override
            public void onDelete(BlogItem item) {
                blogItemList.remove(item);
                adapter.notifyDataSetChanged();
                BlogDao.delete(item.id);
            }
        });
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(this));
    }

    @OnClick(R.id.add_btn)
    void onAddBtnClick() {
        final View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_blog_edit, null);
        final EditText urlEdt = contentView.findViewById(R.id.url_edt);
        final EditText countEdit = contentView.findViewById(R.id.count_edit);
        new AlertDialog.Builder(this)
                .setView(contentView)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = urlEdt.getText().toString();
                        if (TextUtils.isEmpty(countEdit.getText().toString())) {
                            Toast.makeText(SimpleBlogKillerActivity.this, "count cant be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int count = Integer.parseInt(countEdit.getText().toString());
                        if (TextUtils.isEmpty(url)) {
                            Toast.makeText(SimpleBlogKillerActivity.this, "url cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (count <= 0) {
                            Toast.makeText(SimpleBlogKillerActivity.this, "count must > 0", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.cancel();
                        BlogItem blogItem = new BlogItem();
                        blogItem.url = url;
                        blogItem.count = count;
                        blogItemList.add(blogItem);
                        adapter.update(blogItemList);
                        BlogDao.insertOrUpdateBlogItem(blogItem);
                    }
                }).show();
    }

}
