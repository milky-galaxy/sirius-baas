package com.sirius.baas.entries.web.eci;

import com.alibaba.fastjson.JSONObject;
import com.sirius.baas.base.Api;

public class Apply implements Api {

    @Override
    public JSONObject call(JSONObject param) throws Throwable {
        JSONObject out = new JSONObject();
        out.put("EciContainerGroupId", "xxxxx");
        out.put("FcHostPrivateKey", param.get("FcHostPrivateKey").toString());
        return out;
    }

}
