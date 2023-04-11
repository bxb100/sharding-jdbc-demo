package com.example.shardingjdbcdemo;

import com.example.shardingjdbcdemo.entity.User;
import com.example.shardingjdbcdemo.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@SpringBootTest
public class ReadWriteTest {


	@Autowired
	private UserMapper userMapper;


	/**
	 * 写入数据的测试
	 */
	@Test
	public void testInsert() throws SQLException {
		User user = new User();
		user.setUname("张三丰");

		userMapper.insert(user);
	}

	/**
	 * - Junit 测试加事务会回滚
	 * - 开启事务后, 读取和插入都是从主库
	 * - 没有事务的情况下, 读从从库, 写从主库, 看 shardingSphere 的 actual sql 日志
	 */
	@Test
	@Transactional
	public void testTransaction() {

		User user = new User();
		user.setUname("SSSKKK");
		userMapper.insert(user);

		List<User> users = userMapper.selectList(null);
		System.out.println(users);
	}

	/**
	 * 读数据测试, 注意配置文件配置的轮询, 所以从 slave1 到 slave2
	 */
	@Test
	public void testSelectAll() {
		List<User> users = userMapper.selectList(null);
		users = userMapper.selectList(null);//执行第二次测试负载均衡
		users.forEach(System.out::println);

	}
}
