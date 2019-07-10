package com.sirius.baas.entity.dns;

import com.sirius.baas.entity.AliyunReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDomainReq extends AliyunReq {

    private String domainName;

}
