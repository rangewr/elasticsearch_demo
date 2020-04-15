/*
 * @Author: wangran
 * @Date: 2020-04-10 12:48:40
 * @LastEditors: wangran
 * @LastEditTime: 2020-04-15 09:02:45
 */
package com.example.elastic.demo.elasticsearch_demo.entity;

public class ElasticResult {

    private String pwd;

    private String index; // 索引

    private int id;
     
    private String shopcode;

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPwd() {
        return this.pwd;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShopcode() {
        return shopcode;
    }

    public void setShopcode(String shopcode) {
        this.shopcode = shopcode;
    }

}