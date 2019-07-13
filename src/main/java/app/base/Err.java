package app.base;

import com.alibaba.fastjson.JSONObject;

/**
 * 错误类型
 */
public enum Err {

    /**
     * 未知错误
     */
    UNKNOWN("UNKNOWN", "未知错误", "unknown error"),
    /**
     * 断言错误
     */
    ASSERT("ASSERT", "断言错误", "assert error"),
    /**
     * 参数错误
     */
    PARAM("PARAM", "参数错误", "parameters error");

    /**
     * 错误编码
     */
    private String code;
    /**
     * 中文错误提示
     */
    private String cn;
    /**
     * 英文错误提示
     */
    private String en;

    /**
     * 私有构造方法
     */
    private Err(String code, String cn, String en) {
        this.code = code;
        this.cn = cn;
        this.en = en;
    }

    @Override
    public String toString() {
        return this.code;
    }

    /**
     * 获取多语言的错误描述信息（支持用户参数的按需扩展生成不同的错误描述信息的扩展描述）
     */
    public JSONObject getMsg(Object param) {
        JSONObject lang = new JSONObject();
        lang.put("cn", this.cn);
        lang.put("en", this.en);
        return lang;
    }

    public JSONObject getMsg() {
        return this.getMsg(null);
    }

}
