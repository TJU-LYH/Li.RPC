# Li.RPC
# Introduction
本人学习Netty后决定自己写1个基于Netty、Zookeeper、Spring的轻量级RPC框架，收获颇丰，不过本人才疏学浅，难免有所疏漏，若有批评和建议请发到邮箱2683056515@qq.com
# Features
- 支持长连接
- 支持异步调用
- 支持心跳检测
- 支持JSON序列化
- 接近零配置，基于注解实现调用
- 基于Zookeeper实现服务注册中心
- 支持客户端连接动态管理
- 支持客户端服务监听、发现功能
- 支持服务端服务注册功能
- 基于Netty4.X版本实现
# Quick Start
### 服务端开发 
- 在服务端的Service下添加你自己的Service,并加上@Service注解
```
  @Service
  public class TestService {
  	public void test(User user){
  		System.out.println("调用了TestService.test");
  	}
  }
```
- 生成1个服务接口并生成1个实现该接口的类

接口如下
```
  public interface TestRemote {
  	public Response testUser(User user);  
  }
```
实现类如下，为你的实现类添加@Remote注解，该类是你真正调用服务的地方，你可以生成自己想返回给客户端的任何形式的Response
```
  @Remote
  public class TestRemoteImpl implements TestRemote{
  	@Resource
  	private TestService service;
  	public Response testUser(User user){
  		service.test(user);
  		Response response = ResponseUtil.createSuccessResponse(user);
  		return response;
  	}
  }	
```

### 客户端开发
- 在客户端生成一个接口，该接口为你要调用的接口
```
  public interface TestRemote {
  	public Response testUser(User user);
  }
```
### 使用
- 在你要调用的地方生成接口形式的属性，为该属性添加@RemoteInvoke注解
```
  @RunWith(SpringJUnit4ClassRunner.class)
  @ContextConfiguration(classes=RemoteInvokeTest.class)
  @ComponentScan("\\")
  public class RemoteInvokeTest {
  	@RemoteInvoke
  	public static TestRemote userremote;
  	public static User user;
  	@Test
  	public void testSaveUser(){
  		User user = new User();
  		user.setId(1000);
  		user.setName("张三");
  		userremote.testUser(user);
  	}
  }
```
# Overview
![image](https://github.com/TJU-LYH/Li.RPC/assets/140412999/ee22f4ac-0b69-47ea-97e4-9a13b58d1799)


	
  

  
