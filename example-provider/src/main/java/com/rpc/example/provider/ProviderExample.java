package com.rpc.example.provider;

import com.rpc.example.bootstrap.ProviderBootstrap;
import com.rpc.example.common.service.UserService;
import com.rpc.example.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 15304
 */
public class ProviderExample {

    public static void main(String[] args) {
        // 定义要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo<UserService> serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
