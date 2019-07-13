package app.api.dns;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRequest;
import com.aliyuncs.http.HttpResponse;
import app.base.Api;
import app.entity.AliyunRes;
import app.entity.dns.AddDomainReq;
import app.util.AliyunUtil;

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
