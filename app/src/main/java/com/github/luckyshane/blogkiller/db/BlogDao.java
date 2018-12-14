package com.github.luckyshane.blogkiller.db;

import com.github.luckyshane.blogkiller.App;
import com.github.luckyshane.blogkiller.bean.BlogItem;

import java.util.List;

import io.objectbox.Box;

public class BlogDao {

    public static void insertOrUpdateBlogItem(BlogItem newItem) {
        getBlogItemBox().put(newItem);
    }

    public static List<BlogItem> getAll() {
        return getBlogItemBox().getAll();
    }

    public static void delete(long id) {
        getBlogItemBox().remove(id);
    }

    private static Box<BlogItem> getBlogItemBox() {
        return App.getInstance().getBoxStore().boxFor(BlogItem.class);
    }

}
