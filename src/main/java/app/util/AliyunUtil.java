package app.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import app.entity.AliyunReq;

public class AliyunUtil {

    final static private String regionId = "cn-hangzhou";

    public static IAcsClient getClient(AliyunReq req) {
        DefaultProfile.addEndpoint(regionId, "Alidns", "alidns.aliyuncs.com");
        IClientProfile profile = DefaultProfile.getProfile(regionId, req.getAccessKey(), req.getAccessSecret());
        return new DefaultAcsClient(profile);
    }


}
