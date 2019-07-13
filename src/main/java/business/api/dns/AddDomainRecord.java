package business.api.dns;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRecordRequest;
import com.aliyuncs.http.HttpResponse;
import framework.base.Api;
import framework.entity.AliyunRes;
import framework.entity.dns.AddDomainRecordReq;
import business.module.AliyunUtil;

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
