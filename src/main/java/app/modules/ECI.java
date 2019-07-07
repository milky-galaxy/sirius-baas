package app.modules;

import app.base.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.normandy.gw.aliyun.sdk.core.AcsRequestBuilder;
import com.alibaba.normandy.gw.aliyun.sdk.core.ProductNamespace;
import com.aliyuncs.eci.model.v20180808.CreateContainerGroupRequest;
import com.aliyuncs.eci.model.v20180808.CreateContainerGroupRequest.HostAliase;
import com.aliyuncs.eci.model.v20180808.CreateContainerGroupRequest.Volume.ConfigFileVolumeConfigFileToPath;
import com.aliyuncs.eci.model.v20180808.DeleteContainerGroupRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.regions.ProductDomain;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.RpcAcsRequest;
import com.aliyuncs.eci.model.v20180808.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ECI {

}


