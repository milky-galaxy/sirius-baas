package com.sirius.baas.entity;

import com.alibaba.fastjson.JSONObject;
import com.sirius.baas.base.ApiRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunReq implements ApiRequest {

    private String accessKey;
    private String accessSecret;

    @Override
    public ApiRequest fromJson(JSONObject json) {
        return json.toJavaObject(this.getClass());
    }
}
