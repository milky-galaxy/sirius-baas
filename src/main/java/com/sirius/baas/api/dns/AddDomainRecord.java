package com.sirius.baas.api.dns;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRecordRequest;
import com.aliyuncs.http.HttpResponse;
import com.sirius.baas.base.Api;

public class AddDomainRecord extends Api {

    @Override
    public JSONObject call(JSONObject param) throws Throwable {
        IAcsClient client = getClient(param.getString("accessKey"), param.getString("accessSecret"));
        String domainName = param.getString("domainName");
        String targetDomain = param.getString("targetDomain");

        AddDomainRecordRequest request = new AddDomainRecordRequest();
        request.setType("CNAME");
        request.setRR("@");
        request.setLine("default");
        request.setTTL(600L);
        request.setDomainName(domainName);
        request.setValue(targetDomain);
        HttpResponse response = client.doAction(request);

        JSONObject out = new JSONObject();
        out.put("result", response.getHttpContentString());
        return out;
    }

}
