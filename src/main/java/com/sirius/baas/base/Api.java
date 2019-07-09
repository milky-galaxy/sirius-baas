package com.sirius.baas.base;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

abstract public class Api {

    final private String regionId = "cn-hangzhou";

    /**
     * API入口方法
     */
    abstract public JSONObject call(JSONObject param) throws Throwable;

    protected IAcsClient getClient(String accessKeyId, String accessKeySecret) {
        DefaultProfile.addEndpoint(regionId, "Alidns", "alidns.aliyuncs.com");
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        return new DefaultAcsClient(profile);
    }

}
