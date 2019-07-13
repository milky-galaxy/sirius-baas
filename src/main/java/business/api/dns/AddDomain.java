package business.api.dns;


import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRequest;
import com.aliyuncs.http.HttpResponse;
//import framework.$;
//import framework.$;
import framework.$;
import framework.base.Api;
import framework.entity.AliyunRes;
import framework.entity.dns.AddDomainReq;
import business.module.AliyunUtil;

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
