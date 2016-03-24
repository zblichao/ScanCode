package com.lichao.scancode.entity;

/**
 * Created by zblichao on 2016-03-10.
 */
public class NameValuePair {
    private String name;
    private String value;

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
