package com.github.luckyshane.blogkiller.bean;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class BlogItem {
    @Id
    public long id;
    public int count;
    public String url;
}
