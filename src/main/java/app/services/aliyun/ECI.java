package app.services.aliyun;

import app.base.$;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.AcsRequest;
import com.aliyuncs.AcsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.RpcAcsRequest;
import com.aliyuncs.eci.model.v20180808.*;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.regions.ProductDomain;
import com.aliyuncs.eci.model.v20180808.CreateContainerGroupRequest;
import com.aliyuncs.eci.model.v20180808.CreateContainerGroupRequest.*;
import com.aliyuncs.eci.model.v20180808.DeleteContainerGroupRequest;
import com.aliyuncs.eci.model.v20180808.DescribeContainerGroupsRequest;
import com.aliyuncs.eci.model.v20180808.DescribeContainerLogRequest;
import com.aliyuncs.eci.model.v20180808.DescribeContainerLogResponse;
import com.aliyuncs.eci.model.v20180808.DescribeContainerGroupsRequest;
import com.aliyuncs.eci.model.v20180808.DescribeContainerGroupsResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.ArrayStack;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// TODO 云产品的原子接口能力的简化封装
public class ECI {

}
