package com.jadyer.seed.mpp.sdk.weixin.model.template;

import java.util.HashMap;

public class WeixinTemplateMsg {
    private String touser;
    private String template_id;
    private String url;
    private DataItem data;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DataItem getData() {
        return data;
    }

    public void setData(DataItem data) {
        this.data = data;
    }

    public static class DataItem extends HashMap<String, DItem> {
        private static final long serialVersionUID = -5767034886837670403L;
        public DataItem() {}
        public DataItem(String key, DItem item) {
            this.put(key, item);
        }
    }

    public static class DItem {
        private String value;
        private String color;
        public DItem(String value) {
            this.value = value;
        }
        public DItem(String value, String color) {
            this.value = value;
            this.color = color;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}
