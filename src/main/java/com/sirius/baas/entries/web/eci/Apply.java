package com.sirius.baas.entries.web.eci;

import com.alibaba.fastjson.JSONObject;

public class Apply {
    /**
     * 申请ECI资源实例
     *
     * @param param
     *              // 绑定当前用户的网盘信息
     *              NasUserRootPath string  NAS盘上的用户根路径地址实现
     *              FcHostInfo      string  FC创建的host主机配置信息.host.json文件内容需要在IDE_HOME根目录下
     *              FcHostPrivateKey string FC的主机私钥/root/.ssh/id_rsa文件内容
     *              GitPrivateKey   string  用户的GIT仓库的私钥信息
     *              RecoreImageCfgParam string recore镜像的初始化配置参数信息
     * @return EciContainerGroupId string 返回创建的ECI容器资源实例ID
     */
    public static JSONObject $$(JSONObject param) throws Throwable {
        System.out.println(param);

        JSONObject out = new JSONObject();
        out.put("EciContainerGroupId", "xxxxx");
        out.put("FcHostPrivateKey", param.get("FcHostPrivateKey").toString());
        return out;
    }
}


