package com.sirius.baas.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.fc.runtime.Context;
import com.sirius.baas.util.ConfUtil;
import com.sirius.baas.util.ErrUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Router {

    public void go(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JSONObject input = JSON.parseObject(streamToString(inputStream));
        try {
            // 路由方法实现
            JSONObject out = call(context, input.get("api").toString(),
                input.get("param") != null ? (JSONObject) input.get("param") : new JSONObject());
            outputStream.write(out.toJSONString().getBytes());
        } catch (Throwable e) {
            outputStream.write(ErrUtil.encode(e).toJSONString().getBytes());
        }
    }

    private JSONObject call(Context context, String api, JSONObject param) throws Throwable {
        // 根据API路径找到实现类 path/to/Clazz => com.sirius.baas.api.path.to.Clazz
        Class<?> clazz = Class.forName("com.sirius.baas.api" + api.replace('/', '.'));
        java.lang.reflect.Method method = clazz.getMethod("call", JSONObject.class);
        try {
            // 校验形参中的环境变量输入正确合法性（在接口中定义__env__参数进行约定传递环境参数）
            // 默认的环境参数配置为local本地调试使用
            ConfUtil.ENV = param.get(ConfUtil.PARAM_ENV) != null ? param.get(ConfUtil.PARAM_ENV).toString() : "local";
            // 清空上次请求对应的全局环境变量值（模拟PHP的单线程处理框架将全局变量等同于请求上下文信息）
            ConfUtil.CONFIG = null;
            // 路由调用实现以及输出结果响应输出
            JSONObject content = (JSONObject) method.invoke(null, param);
            JSONObject out = new JSONObject();
            // 避免前端空对象处理
            out.put("content", content != null ? content : new JSONObject());
            out.put("success", true);
            return out;
        } catch (Exception e) {
            // 打印反射调用实现对应的原始的异常信息（以便于正确得到对应的错误栈信息）
            context.getLogger().error(exceptionToString(e));
            return ErrUtil.encode(e);
        }
    }

    private String streamToString(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private String exceptionToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return e.toString() + System.lineSeparator() + sw.toString();
    }

}
