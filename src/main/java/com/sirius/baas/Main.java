package com.sirius.baas;

import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.FunctionInitializer;
import com.aliyun.fc.runtime.StreamRequestHandler;
import com.sirius.baas.base.Router;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main implements FunctionInitializer, StreamRequestHandler {

    private Router router = new Router();

    @Override
    public void initialize(Context context) throws IOException {
        FunctionComputeLogger logger = context.getLogger();
        logger.debug(String.format("Init request is %s %n", context.getRequestId()));
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        router.go(inputStream, outputStream, context);
    }

}
