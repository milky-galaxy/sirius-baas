package com.sirius.baas.api.dns;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRequest;
import com.aliyuncs.http.HttpResponse;
import com.sirius.baas.base.Api;

public class AddDomain extends Api {

    @Override
    public JSONObject call(JSONObject param) throws Throwable {
        IAcsClient client = getClient(param.getString("accessKey"), param.getString("accessSecret"));
        String domainName = param.getString("domainName");

        AddDomainRequest request = new AddDomainRequest();
        request.setDomainName(domainName);
        HttpResponse response = client.doAction(request);

        JSONObject out = new JSONObject();
        out.put("result", response.getHttpContentString());
        return out;
    }

}
