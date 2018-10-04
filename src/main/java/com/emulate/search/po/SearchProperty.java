package com.emulate.search.po;

import java.util.Map;

public class SearchProperty {

    private String index;//数据库名
    private String type;//表名
    private String []fields;//需要检索的属性
    private boolean not; //是否标记了非
    private boolean or;//是否修改连接词为OR
    private Map<String,String> param; //参数列表，构建多属性where
    private String orderByStr; //排序部分
    private String groupByStr; //分组部分
    private String keywords;//关键字


    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public boolean isNot() {
        return not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public boolean isOr() {
        return or;
    }

    public void setOr(boolean or) {
        this.or = or;
    }

    public Map<String, String> getParam() {
        return param;
    }

    public void setParam(Map<String, String> param) {
        this.param = param;
    }

    public String getOrderByStr() {
        return orderByStr;
    }

    public void setOrderByStr(String orderByStr) {
        this.orderByStr = orderByStr;
    }

    public String getGroupByStr() {
        return groupByStr;
    }

    public void setGroupByStr(String groupByStr) {
        this.groupByStr = groupByStr;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
