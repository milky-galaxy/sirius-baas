package framework.entity.dns;

import framework.entity.AliyunReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddDomainReq extends AliyunReq {

    private String domainName;

    public String getDomainName() {
        return domainName;
    }
}
