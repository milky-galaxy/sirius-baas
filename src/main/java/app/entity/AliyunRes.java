package app.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import app.base.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunRes implements ApiResponse {

    private String result;

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public JSONObject toJson() {
        return JSON.parseObject(JSON.toJSONString(this));
    }
}
