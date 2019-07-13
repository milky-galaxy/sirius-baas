package app.base;

import com.alibaba.fastjson.JSONObject;

public interface ApiRequest {

    ApiRequest fromJson(JSONObject json);

}
