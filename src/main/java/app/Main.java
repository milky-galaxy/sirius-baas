package app;

import app.base.ERR;
import app.base.$;
import app.services.aliyun.ECI;
import com.alibaba.fastjson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.util.*;

import java.net.InetSocketAddress;
import java.util.concurrent.Semaphore;

public class Main {
    static public Semaphore __LOCK__ = new Semaphore(1);

    public void handleRequest() throws IOException {
//        try {
//            // 统一处理JSON请求信息 FIXME 是否最优有待找FC同学优化一下
//            BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//            StringBuilder responseStrBuilder = new StringBuilder();
//            String inputStr;
//            while ((inputStr = streamReader.readLine()) != null)
//                responseStrBuilder.append(inputStr);
//            JSONObject in = JSON.parseObject(responseStrBuilder.toString());
//
//            // 自动方法路由实现
//            JSONObject out = xcall(in.get("api").toString(),
//                    in.get("param") != null ? (JSONObject) in.get("param") : new JSONObject());
//
//            outputStream.write(out.toJSONString().getBytes());
//        } catch (Throwable e) {
//            outputStream.write(ERR.encode(e).toJSONString().getBytes());
//        }
    }

    // 自定义路由的静态类正常逻辑调用方法实现
    private static JSONObject __xcall__(String api, JSONObject param) throws Throwable {
        // 自动方法路由实现
        // 静态类的方法调用 http://xxxx/web/api/Release => app.entries.web.api.Release
        Class<?> clazz = Class.forName("app.entries" + api.replace('/', '.'));
        java.lang.reflect.Method method = clazz.getMethod("$$", JSONObject.class);
        try {
            // 校验形参中的环境变量输入正确合法性（在EPAAS的每个接口中定义__env__参数进行约定传递环境参数）
            if (param.get("__env__") != null) {
                switch (param.get("__env__").toString()) {
                    case "local":
                    case "daily":
                    case "pre":
                    case "gray":
                    case "prod":
                        // 研发约定只支持这几种环境配置，后续如果遇到特殊的需求再添加。
                        $.__ENV__ = param.get("__env__").toString(); // 补上当前环境变量配置参数
                        break;
                    default:
                        throw new Exception("__env__ param error => " + param.get("__env__").toString());
                }
            } else {
                // 默认的环境参数配置为local本地调试使用
                $.__ENV__ = "local";
            }
            // 清空上次请求对应的全局环境变量值（模拟PHP的单线程处理框架将全局变量等同于请求上下文信息）
            $.__CONFIG__ = null;

            // 路由调用实现以及输出结果响应输出
            JSONObject content = (JSONObject) method.invoke(null,
                    param != null ? param : new JSONObject());
            JSONObject out = new JSONObject();
            out.put("success", true);
            out.put("content", content != null ? content : new JSONObject()); // 避免前端空对象处理
            return out;
        } catch (java.lang.reflect.InvocationTargetException e) {
            // 抛出反射调用实现对应的原始的异常信息（以便于正确得到对应的错误栈信息）
            throw e.getTargetException();
        }
    }

    // 自定义路由的静态类正常逻辑调用方法实现
    private static JSONObject xcall(String api, JSONObject param) throws Throwable {
        return __xcall__(api, param);
    }

    private static JSONObject xcall(String api) throws Throwable {
        return __xcall__(api, null);
    }

    // 进行本地JAVA服务的功能模拟以及单步调试（启动一个8080服务器模拟java微服务的接口调用实现）
    public static void main(String[] args) {
        try {
//            ECI.RestartContainerGroup("eci-uf69siqpfohnr9syox0w");
//            ECI.CreateEciInstance();
//            ECI.UpdateEciInstanceTest("eci-uf6griggln8ikiofo1ff");
//            ECI.DescribeContainerLogs("eci-uf67tgug84geu9ps03o7", "wujie-test");
//            ECI.DescribeContainerGroups(new String[]{"eci-uf6e0l0q1u7vi7eb9p98"});
//            ECI.UpdateEciInstance("eci-uf6cx6fkacyd0e4b9zv0");
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new MyHandler()); // 创上下文监听, "/" 表示匹配所有 URI 请求
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    static class MyRunnable implements Runnable {

        private HttpExchange _t;

        MyRunnable(HttpExchange t) {
            _t = t;
        }

        @Override
        public void run() {
            try {
                // 确保调试环境下每个请求严格的串行化处理（模拟线上单个请求的FaaS环境严格串行化处理请求隔离机制设计）
                __LOCK__.acquire();
                __handle__(_t);
            } catch (Exception e) {
                // TODO 写日志以及监控日志报警消息处理
                try {
                    String response = "unknown error";
                    byte[] bs = response.getBytes("UTF-8");
                    _t.sendResponseHeaders(500, bs.length);
                    OutputStream os = _t.getResponseBody();
                    os.write(bs);
                    os.close();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            } finally {
                __LOCK__.release();
            }
        }
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestURI().toString().equalsIgnoreCase("/favicon.ico")) {
                return;
            }
//            System.out.println(t.getRequestURI());
            new Thread(new MyRunnable(t)).start();
        }
    }

    public static void __handle__(HttpExchange t) throws IOException {
        // 设置响应头
        t.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        // 过滤掉所有非POST请求数据
        if (!t.getRequestMethod().equalsIgnoreCase("POST")) {
            String response = ERR.encode(new Throwable("should request by post method")).toJSONString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            return;
        }

        // 得到请求的完整路径信息
        String api = t.getRequestURI().toString();
        JSONObject out;
        try {
            // 统一处理JSON请求信息 FIXME 是否最优有待找FC同学优化一下
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(t.getRequestBody(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            JSONObject in;
            // TODO 不应该忽略参数解析错误
            if (t.getRequestHeaders().get("Content-type").get(0).toLowerCase().startsWith("application/x-www-form-urlencoded")) {
                in = JSONObject.parseObject(JSON.toJSONString(splitQuery(responseStrBuilder.toString())));
            } else {
                in = JSON.parseObject(responseStrBuilder.toString());
            }
            if (in != null) {
                out = xcall(api, in);
            } else {
                out = xcall(api);
            }
        } catch (Throwable e) {
            out = ERR.encode(e);
        }
        try {
            String response = out.toJSONString();
            byte[] bs = response.getBytes("UTF-8");
            t.sendResponseHeaders(200, bs.length);
            OutputStream os = t.getResponseBody();
            os.write(bs);
            os.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> splitQuery(String query) {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(pair.substring(0, idx), pair.substring(idx + 1));
        }
        return query_pairs;
    }
}
