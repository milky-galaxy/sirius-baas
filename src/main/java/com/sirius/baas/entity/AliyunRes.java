package com.sirius.baas.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sirius.baas.base.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunRes implements ApiResponse {

    private String result;

    @Override
    public JSONObject toJson() {
        return JSON.parseObject(JSON.toJSONString(this));
    }
}
