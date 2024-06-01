package com.rpc.examplespringbootconsumer.consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;

@SpringBootTest
class ExampleServiceConsumerTest {

    @Resource
    private ExampleServiceConsumer serviceConsumer;

    @Test
    void test1() {
        serviceConsumer.test();
    }
}