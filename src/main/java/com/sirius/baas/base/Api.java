package com.sirius.baas.base;

import com.alibaba.fastjson.JSONObject;

public interface Api {

    /**
     * API入口方法
     */
    JSONObject call(JSONObject param) throws Throwable;

}
