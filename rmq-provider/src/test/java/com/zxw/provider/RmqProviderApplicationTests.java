package com.zxw.provider;

import entity.MsgInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RmqProviderApplicationTests {

	@Autowired
	private Provider provider;

	@Test
	void contextLoads() {

		MsgInfo info = new MsgInfo("我是消息1","10001","00001");
		System.out.println("****************开始发送消息******************");
		provider.send(info);
		System.out.println("****************结束发送消息******************");

	}

}
