package com.sirius.baas.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.sirius.baas.base.Err;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import com.sirius.baas.Main;

public final class ConfUtil {

    private static final String CONF_FOLDER = "/com/sirius/baas/config/";
    public static final String PARAM_ENV = "__env__";

    /**
     * 当前应用运行环境的全局配置参数（local,daily,pre,prod,...）
     */
    public static String ENV = "local";
    public static JSONObject CONFIG = null;

    public static Object get(String path) throws Throwable {
        return doGet(path, null);
    }

    public static Object get(String path, Object defaultValue) throws Throwable {
        return doGet(path, defaultValue);
    }

    public static void xassert(boolean expr, Err e, Object param) throws Throwable {
        doAssert(expr, e, param);
    }

    public static void xassert(boolean expr, Object param) throws Throwable {
        doAssert(expr, Err.ASSERT, param);
    }

    public static void xassert(boolean expr, Err e) throws Throwable {
        doAssert(expr, e, null);
    }

    public static void xassert(boolean expr) throws Throwable {
        doAssert(expr, Err.ASSERT, null);
    }

    /**
     * 配置参数读取的统一代码路径参数实现（根据环境自动识别读取配置数据）
     */
    private static Object doGet(String path, Object defaultValue) throws Throwable {
        try {
            // java JSONObject进行merge操作实现配置项目的合并处理算法实现
            if (CONFIG == null) {
                InputStream stream = Main.class.getResourceAsStream(CONF_FOLDER + "config.base.yaml");
                Yaml yaml2 = new Yaml();
                Map<String, Object> tmp2 = yaml2.load(stream);
                // 首次初始化全局配置数据信息
                InputStream baseConfigPath = Main.class.getResourceAsStream(CONF_FOLDER + "config.base.yaml");
                InputStream targetConfigPath = Main.class.getResourceAsStream(CONF_FOLDER + "config." + ENV + ".yaml");
                JSONObject base;
                JSONObject target;
                Yaml yaml = new Yaml();
                // 基础文件读取配置
                Map<String, Object> tmp = yaml.load(baseConfigPath);
                base = tmp == null ? new JSONObject() : new JSONObject(tmp);
                base = JSON.parseObject(base.toJSONString());
                // 目标文件读取配置
                tmp = yaml.load(targetConfigPath);
                target = tmp == null ? new JSONObject() : new JSONObject(tmp);
                target = JSON.parseObject(target.toJSONString());
                // 合并基础配置文件信息
                CONFIG = jsonDeepMerge(target, base);
            }
            // "a. b[ 1]" => a.b[1] => a,b[1] => 深度递归找到需要查找的配置字段信息
            String key = "$." + path.replace(" ", "");
            Object tmp = JSONPath.eval(CONFIG, key);
            if (tmp == null) {
                return defaultValue;
            }
            return tmp;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Merge "source" into "target". If fields have equal name, merge them recursively.
     *
     * @return the merged object (target).
     */
    private static JSONObject jsonDeepMerge(JSONObject source, JSONObject target) throws Throwable {
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (!target.containsKey(key)) {
                // new value for "key":
                target.put(key, value);
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject) value;
                    jsonDeepMerge(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
        }
        return target;
    }

    private static void doAssert(boolean expr, Err e, Object param) throws Throwable {
        if (!expr) {
            JSONObject msg = new JSONObject();
            msg.put("code", e.toString());
            msg.put("msg", e.getMsg());
            JSONArray stack = new JSONArray();
            StackTraceElement[] elems = new Throwable().getStackTrace();
            for (int i = 2; i < elems.length; i++) {
                stack.add(elems[i].getClassName() + ":" + elems[i].getMethodName() +
                    "@" + elems[i].getFileName() + ":" + elems[i].getLineNumber());
            }
            msg.put("param", param);
            msg.put("stack", stack);
            throw new Throwable(msg.toJSONString());
        }
    }
}

