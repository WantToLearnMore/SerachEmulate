package com.emulate.search.po;

public class RequestInfo {
    private int pageNo=1;//页码 默认为第一页
    private int pageSize=10;//每页大小 默认为每页10条数据
    private String sourceName;//资源名字
    private String sourceType;//资源类型
    private String author;//作者
    private int operition=1;//操作类型 默认为or

    @Override
    public String toString() {
        return "RequestInfo{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", sourceName='" + sourceName + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", author='" + author + '\'' +
                ", operition=" + operition +
                '}';
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getOperition() {
        return operition;
    }

    public void setOperition(int operition) {
        this.operition = operition;
    }
}
