package com.emulate.search.po.elasticPO;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Document(indexName = "searchemulate",type = "audio")
public class Audio implements Serializable {

    @Id
    private int id;//数据库中的id

    private String name;//音频名字
    private int star;//音频热度
    private Date publish;//发行时间
    private String url;//音频在文件系统中的存储url
    private String author;//歌手
    private String description;//简介
    private String cover;
    private int category;//音乐类型
    private int status;
    private Date creatTime;//上传时间
    private String language;//语言

    @Override
    public String toString() {
        return "Audio{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", star=" + star +
                ", publish=" + publish +
                ", url='" + url + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", cover='" + cover + '\'' +
                ", category=" + category +
                ", status=" + status +
                ", creatTime=" + creatTime +
                ", language='" + language + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public Date getPublish() {
        return publish;
    }

    public void setPublish(Date publish) {
        this.publish = publish;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
