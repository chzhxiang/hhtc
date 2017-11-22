package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

import java.util.ArrayList;
import java.util.List;

public class WeixinOutNewsMsg extends WeixinOutMsg {
    private List<WeixinNews> articles = new ArrayList<WeixinNews>();

    public WeixinOutNewsMsg(WeixinInMsg inMsg) {
        super(inMsg);
        this.msgType = "news";
    }

    public int getArticleCount() {
        return articles.size();
    }

    public List<WeixinNews> getArticles() {
        return articles;
    }

    public WeixinOutNewsMsg addNews(String title, String description, String picUrl, String url) {
        this.articles.add(new WeixinNews(title, description, picUrl, url));
        return this;
    }

    public static class WeixinNews {
        private String title;
        private String description;
        private String picUrl;
        private String url;

        public WeixinNews(String title, String description, String picUrl, String url) {
            this.title = title;
            this.description = description;
            this.picUrl = picUrl;
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}