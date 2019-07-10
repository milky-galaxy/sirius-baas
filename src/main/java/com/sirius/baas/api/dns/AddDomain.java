package com.sirius.baas.api.dns;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRequest;
import com.aliyuncs.http.HttpResponse;
import com.sirius.baas.base.Api;
import com.sirius.baas.entity.AliyunRes;
import com.sirius.baas.entity.dns.AddDomainReq;
import com.sirius.baas.util.AliyunUtil;

public class AddDomain implements Api<AddDomainReq, AliyunRes> {

    @Override
    public AliyunRes call(AddDomainReq param) throws Throwable {
        IAcsClient client = AliyunUtil.getClient(param);

        AddDomainRequest request = new AddDomainRequest();
        request.setDomainName(param.getDomainName());
        HttpResponse response = client.doAction(request);

        AliyunRes out = new AliyunRes();
        out.setResult(response.getHttpContentString());
        return out;
    }

}
