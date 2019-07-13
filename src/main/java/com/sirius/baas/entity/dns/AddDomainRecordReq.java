package com.sirius.baas.entity.dns;

import com.sirius.baas.entity.AliyunReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDomainRecordReq extends AliyunReq {

    private String domainName;
    private String targetDomain;

    public String getDomainName() {
        return this.domainName;
    }

    public String getTargetDomain() {
        return this.targetDomain;
    }
}
