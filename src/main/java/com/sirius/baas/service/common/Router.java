package com.sirius.baas.service.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.fc.runtime.Context;
import com.sirius.baas.base.ApiRequest;
import com.sirius.baas.base.ApiResponse;
import com.sirius.baas.util.ConfUtil;
import com.sirius.baas.util.ErrUtil;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Router {

    private static final String API = "api";
    private static final String PARAM = "param";

    public void go(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        String input = streamToString(inputStream);
        JSONObject in = JSON.parseObject(input);
        if (in == null || in.get(API) == null) {
            outputStream.write("400 - Missing api parameter".getBytes());
            return;
        }
        String api = in.get(API).toString();
        JSONObject param = JSONObject.parseObject(in.get(PARAM).toString());
        if (param == null) {
            param = new JSONObject();
        }
        try {
            // 路由方法实现
            outputStream.write(call(api, param).toJSONString().getBytes());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            outputStream.write(("404 - API not exist: " + api).getBytes());
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            outputStream.write(("500 - API cannot invoke: " + api).getBytes());
        } catch (Throwable e) {
            // 打印原始的异常信息（以便于正确得到对应的错误栈信息）
            context.getLogger().error(exceptionToString(e));
            outputStream.write(ErrUtil.encode(e).toJSONString().getBytes());
        }
    }

    private JSONObject call(String api, JSONObject param) throws Throwable {
        setupConf(param);
        // 根据API路径找到实现类 path/to/Clazz => com.sirius.baas.api.path.to.Clazz
        Class<?> clazz = Class.forName("com.sirius.baas.api" + api.replace('/', '.'));
        ParameterizedTypeImpl reqType = (ParameterizedTypeImpl)clazz.getAnnotatedInterfaces()[0].getType();
        Class reqClazz = Class.forName(reqType.getActualTypeArguments()[0].getTypeName());
        Method method = clazz.getMethod("call", reqClazz);
        ApiRequest req = (ApiRequest)reqClazz.newInstance();
        ApiResponse content = (ApiResponse) method.invoke(clazz.newInstance(), req.fromJson(param));
        JSONObject out = new JSONObject();
        // 避免前端空对象处理
        out.put("content", content != null ? content.toJson() : new JSONObject());
        out.put("success", true);
        return out;
    }

    private void setupConf(JSONObject param) {
        // 校验形参中的环境变量输入正确合法性（在接口中定义__env__参数进行约定传递环境参数）
        // 默认的环境参数配置为local本地调试使用
        ConfUtil.ENV = param.get(ConfUtil.PARAM_ENV) != null ? param.get(ConfUtil.PARAM_ENV).toString() : "local";
        // 清空上次请求对应的全局环境变量值（模拟PHP的单线程处理框架将全局变量等同于请求上下文信息）
        ConfUtil.CONFIG = null;
        // 路由调用实现以及输出结果响应输出
    }

    private String streamToString(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private String exceptionToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return e.toString() + System.lineSeparator() + sw.toString();
    }

}
