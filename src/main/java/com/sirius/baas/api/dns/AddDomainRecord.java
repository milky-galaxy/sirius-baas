package com.sirius.baas.api.dns;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRecordRequest;
import com.aliyuncs.http.HttpResponse;
import com.sirius.baas.base.Api;
import com.sirius.baas.entity.AliyunRes;
import com.sirius.baas.entity.dns.AddDomainRecordReq;
import com.sirius.baas.util.AliyunUtil;

public class AddDomainRecord implements Api<AddDomainRecordReq, AliyunRes> {

    @Override
    public AliyunRes call(AddDomainRecordReq param) throws Throwable {
        IAcsClient client = AliyunUtil.getClient(param);

        AddDomainRecordRequest request = new AddDomainRecordRequest();
        request.setType("CNAME");
        request.setRR("@");
        request.setLine("default");
        request.setTTL(600L);
        request.setDomainName(param.getDomainName());
        request.setValue(param.getTargetDomain());
        HttpResponse response = client.doAction(request);

        AliyunRes out = new AliyunRes();
        out.setResult(response.getHttpContentString());
        return out;
    }

}
