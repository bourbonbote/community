package com.bobo.community;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
class CommunityApplicationTests {

	@Test
	void contextLoads() {
	}


	/*@Resource
	DataSource dataSource;
	@Test
	public void test() throws SQLException {
		Connection connection = dataSource.getConnection();
		DatabaseMetaData metaData = connection.getMetaData();

		//数据源>>>>>>class com.zaxxer.hikari.HikariDataSource
		System.out.println("数据源>>>>>>" + dataSource.getClass());
		System.out.println("连接>>>>>>>>" + connection);
		System.out.println("连接地址>>>>" + connection.getMetaData().getURL());
		System.out.println("驱动名称>>>>" + metaData.getDriverName());
		System.out.println("驱动版本>>>>" + metaData.getDriverVersion());
		System.out.println("数据库名称>>" + metaData.getDatabaseProductName());
		System.out.println("数据库版本>>" + metaData.getDatabaseProductVersion());
		System.out.println("连接用户名称>" + metaData.getUserName());

		connection.close();
	}*/

}
