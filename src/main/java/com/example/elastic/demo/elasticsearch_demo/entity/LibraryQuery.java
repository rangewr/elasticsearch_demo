/*
 * @Author: wangran
 * 
 * @Date: 2020-03-06 11:47:11
 * 
 * @LastEditors: wangran
 * 
 * @LastEditTime: 2020-04-15 09:02:58
 */
package com.example.elastic.demo.elasticsearch_demo.entity;

public class LibraryQuery{
    private int currentPage;
    private int libraryId;
    private String queryText;
    private int pageSize;//页面显示数据条数,在系统参数中配置

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getLibraryId() {
        return libraryId;
    }

    public void setLibraryId(int libraryId) {
        this.libraryId = libraryId;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


}