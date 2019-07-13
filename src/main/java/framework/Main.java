package framework;
import framework.base.ConfUtil;
import framework.base.ErrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import framework.base.ApiRequest;
import framework.base.ApiResponse;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.FunctionInitializer;
import com.aliyun.fc.runtime.StreamRequestHandler;

import java.io.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
//import $;

public class Main implements FunctionInitializer, StreamRequestHandler {
    @Override
    public void initialize(Context context) throws IOException {
        FunctionComputeLogger logger = context.getLogger();
        logger.debug(String.format("Init function instance %n"));


    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        go(inputStream, outputStream, context);
    }


    public static void main(String[] args) {
        try {
            // $.__ENV__ = "local"; // 自动识别约定的开发环境
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new MyHandler()); // 创上下文监听, "/" 表示匹配所有 URI 请求
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // 设置响应头
            t.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

            // 过滤掉所有非POST请求数据
            if (!t.getRequestMethod().equalsIgnoreCase("POST")) {
                String response = exceptionToString(new Throwable("should request by post method"));
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
//
            go(t.getRequestBody(), t.getResponseBody(), null);
            t.getResponseBody().close();

            // 得到请求的完整路径信息
            String api = t.getRequestURI().toString();
            JSONObject out = null;
            try {
                // 统一处理JSON请求信息
                String inputStr = streamToString(t.getRequestBody());
//                JSONObject in = JSON.parseObject(inputStr);
                System.out.println(inputStr);
//                out = xcall(api, in);
            } catch (Throwable e) {
//                out = exceptionToString(e);
            }
            try {
//                String response = out.toJSONString();
                String response = "{}";
                byte[] bs = response.getBytes("UTF-8");
                t.sendResponseHeaders(200, bs.length);
                OutputStream os = t.getResponseBody();
                os.write(bs);
                os.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    private static final String API = "business";
    private static final String PARAM = "param";
    private static void go(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        String input = streamToString(inputStream);
        JSONObject in = JSON.parseObject(input);
        if (in == null || in.get(API) == null) {
            outputStream.write("400 - Missing business.api parameter".getBytes());
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

    private static JSONObject call(String api, JSONObject param) throws Throwable {
        setupConf(param);
        // 根据API路径找到实现类 path/to/Clazz => com.sirius.baas.business.api.path.to.Clazz
        Class<?> clazz = Class.forName("com.sirius.baas.business.api" + api.replace('/', '.'));
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

    private static void setupConf(JSONObject param) {
        // 校验形参中的环境变量输入正确合法性（在接口中定义__env__参数进行约定传递环境参数）
        // 默认的环境参数配置为local本地调试使用
        ConfUtil.ENV = param.get(ConfUtil.PARAM_ENV) != null ? param.get(ConfUtil.PARAM_ENV).toString() : "local";
        // 清空上次请求对应的全局环境变量值（模拟PHP的单线程处理框架将全局变量等同于请求上下文信息）
        ConfUtil.CONFIG = null;
        // 路由调用实现以及输出结果响应输出
    }

    private static String streamToString(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private static String exceptionToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return e.toString() + System.lineSeparator() + sw.toString();
    }
}
