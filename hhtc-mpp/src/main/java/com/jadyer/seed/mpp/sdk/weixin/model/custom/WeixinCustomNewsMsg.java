package com.jadyer.seed.mpp.sdk.weixin.model.custom;

public class WeixinCustomNewsMsg extends WeixinCustomMsg {
    private String msgtype;
    private News news;

    public WeixinCustomNewsMsg(String touser, News news) {
        super(touser);
        this.news = news;
        this.msgtype = "news";
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public static class News{
        private Article[] articles;
        public News(Article[] articles) {
            this.articles = articles;
        }
        public Article[] getArticles() {
            return articles;
        }
        public static class Article{
            private String title;
            private String description;
            private String picurl;
            private String url;

            public Article(String title, String description, String picurl, String url) {
                this.title = title;
                this.description = description;
                this.picurl = picurl;
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
            public String getPicurl() {
                return picurl;
            }
            public void setPicurl(String picurl) {
                this.picurl = picurl;
            }
            public String getUrl() {
                return url;
            }
            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}