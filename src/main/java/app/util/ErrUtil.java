package app.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import app.base.Err;

public class ErrUtil {

    /**
     * 错误编码为五元组
     */
    public static JSONObject encode(Throwable e) {
        // 对于异常数据的统一响应处理（参照nodejs的框架bxjs进行实现）
        // 判断是否是JSON字符串信息并且符合开发规范约定
        String msg = e.getMessage();
        try {
            // 正常错误编码格式转换输出
            JSONObject obj = JSON.parseObject(msg);
            if (obj.get("code") == null) {
                // 非正确的编码数据格式忽略掉转为wrapper模式包装处理
                throw new Throwable();
            }
            JSONObject out = new JSONObject();
            out.put("success", false);
            // 按需传递用户错误的一些额外信息内容
            out.put("content", obj.get("param"));
            // 统一错误编码定义
            out.put("errCode", obj.get("code"));
            // 多语言错误提示统一透传交由前端自己定义正确提示（用户自己勾选多语种选择）
            out.put("errMsg", obj.get("msg"));
            // TODO 错误级别定义，参照epaas网关约定
            out.put("errLevel", "error");
            out.put("errStack", obj.get("stack"));
            return out;
        } catch (Throwable ext) {
            // 并非规范的数据格式降级为wrapper模式封装非标准的异常
        }

        // 渠道正确的错误栈
        JSONArray stack = new JSONArray();
        for (StackTraceElement elem : e.getStackTrace()) {
            stack.add(elem.getClassName() + "#" + elem.getMethodName() + "@" + elem.getFileName() + ":" + elem.getLineNumber());
        }
        JSONObject out = new JSONObject();
        out.put("success", false);
        out.put("content", e.toString());
        out.put("errCode", Err.UNKNOWN);
        out.put("errMsg", Err.UNKNOWN.getMsg());
        out.put("errLevel", "error");
        out.put("errStack", stack);
        return out;
    }

}
