package org.popkit.mobile.entity;

import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;

/**
 * @author Aborn Jiang
 * @email aborn.jiang AT foxmail.com
 * @date 06-13-2015
 * @time 10:41 PM
 */
public class MCacheKey implements Serializable {

    private static final long serialVersionUID = 2778076216561702342L;

    /**
     * man key, i.e. cache name
     */
    private String key;

    /**
     * sub key, i.e. element key name
     */
    private Object[] params;

    public MCacheKey(String key, Object... params) {
        this.key = key;
        this.params = params;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubKey() {
        return ArrayUtils.toString(params);
    }

    @Override
    public String toString() {
        return "[key:" + this.key + ", params:" + ArrayUtils.toString(this.params) + "]";
    }
}
