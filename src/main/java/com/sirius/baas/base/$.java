package com.sirius.baas.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import com.sirius.baas.Main;

public final class $ {
    // 当前应用运行环境的全局配置参数（local,daily,pre,prod,...）
    public static String __ENV__ = "local";
    public static JSONObject __CONFIG__ = null;

    /**
     * Merge "source" into "target". If fields have equal name, merge them recursively.
     *
     * @return the merged object (target).
     */
    private static JSONObject __jsonDeepMerge__(JSONObject source, JSONObject target) throws Throwable {
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (!target.containsKey(key)) {
                // new value for "key":
                target.put(key, value);
            } else {
                // existing value for "key" - recursively deep merge:
                if (value instanceof JSONObject) {
                    JSONObject valueJson = (JSONObject) value;
                    __jsonDeepMerge__(valueJson, target.getJSONObject(key));
                } else {
                    target.put(key, value);
                }
            }
        }
        return target;
    }

    // 配置参数读取的统一代码路径参数实现（根据环境自动识别读取配置数据）
    private static Object __xconfig__(String path, Object defaultValue) throws Throwable {
        try {
            // java JSONObject进行merge操作实现配置项目的合并处理算法实现
            if (__CONFIG__ == null) {
                InputStream stream = Main.class.getResourceAsStream("/com/sirius/baas/config/config.base.yaml");
                Yaml yaml2 = new Yaml();
                Map<String, Object> tmp2 = yaml2.load(stream);
//                System.out.println(tmp2);
//                System.out.println("asdf");
                // 首次初始化全局配置数据信息
                InputStream baseConfigPath = Main.class.getResourceAsStream("/com/sirius/baas/config/config.base.yaml");
                InputStream targetConfigPath = Main.class.getResourceAsStream(
                    "/com/sirius/baas/config/config." + __ENV__ + ".yaml");
                JSONObject base;
                JSONObject target;
                Yaml yaml = new Yaml();
                // 基础文件读取配置
                Map<String, Object> tmp = yaml.load(baseConfigPath);
                if (tmp == null) {
                    base = new JSONObject();
                } else {
                    base = new JSONObject(tmp);
                }
                base = new JSONObject(base);
                base = JSON.parseObject(base.toJSONString());
                // 目标文件读取配置
                tmp = yaml.load(targetConfigPath);
                if (tmp == null) {
                    target = new JSONObject();
                } else {
                    target = new JSONObject(tmp);
                }
                target = JSON.parseObject(target.toJSONString());

                // 合并基础配置文件信息
//                System.out.println(base);
//                System.out.println(target);
                __CONFIG__ = __jsonDeepMerge__(target, base);
                System.out.println(__CONFIG__);
            }
            // "a. b[ 1]" => a.b[1] => a,b[1] => 深度递归找到需要查找的配置字段信息
            String key = "$." + path.replace(" ", "");
            Object tmp = JSONPath.eval(__CONFIG__, key);
            if (tmp == null) {
                return defaultValue;
            }
            return tmp;
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Object xconfig(String path, Object defaultValue) throws Throwable {
        return __xconfig__(path, defaultValue);
    }

    public static Object xconfig(String path) throws Throwable {
        return __xconfig__(path, null);
    }

    private static void __xthrow__(ERR e, Object param) throws Throwable {
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

    public static void xthrow(ERR e) throws Throwable {
        __xthrow__(e, null);
    }

    public static void xthrow(ERR e, Object param) throws Throwable {
        __xthrow__(e, param);
    }

    private static void __xassert__(boolean expr, ERR e, Object param) throws Throwable {
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

    public static void xassert(boolean expr, ERR e, Object param) throws Throwable {
        __xassert__(expr, e, param);
    }

    public static void xassert(boolean expr, Object param) throws Throwable {
        __xassert__(expr, ERR.ASSERT, param);
    }

    public static void xassert(boolean expr, ERR e) throws Throwable {
        __xassert__(expr, e, null);
    }

    public static void xassert(boolean expr) throws Throwable {
        __xassert__(expr, ERR.ASSERT, null);
    }
}

