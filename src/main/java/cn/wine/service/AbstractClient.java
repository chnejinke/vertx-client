package cn.wine.service;

import cn.wine.service.zookeeper.ZookeeperClusterManager;
import com.alibaba.fastjson.JSON;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.metrics.MetricsOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.core.spi.cluster.ClusterManager;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Resource;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author ChenJinKe
 */
public abstract class AbstractClient extends AbstractVerticle implements Client, InitializingBean {
	final AtomicReference<NetSocket> NET_SOCKET = new AtomicReference<>();
	final AtomicReference<NetClient> CLIENT = new AtomicReference<>();
	private static final AtomicReference<Vertx> vertx = new AtomicReference<>();

	@Resource
	private CuratorFramework curatorFramework;

	static void printf(String log) {
		System.out.println(log);
	}


	@Override
	public void sendMsg(MyMessage message) {
		// TODO 打断点超时
		String msg = JSON.toJSONString(message);
		printf(msg);
		NET_SOCKET.get().write(Buffer.buffer(msg));
	}

	@Override
	public void close() {
		CLIENT.get().close();
	}

	@Override
	public void start() {
		printf("Client" + getClientName() + "启动");
		// 创建一个TCP客户端
		NetClient client = vertx.get().createNetClient();
		CLIENT.set(client);
		// 连接服务器
		client.connect(5555, "192.168.2.205", conn -> {
			if (conn.succeeded()) {
				NetSocket s = conn.result();
				NET_SOCKET.set(s);
				// 向服务器写数据
				sendMsg(MyMessage.builder()
						.clientId(getClientName())
						.sessionId(UUID.randomUUID().toString())
						.content("注册")
						.type("register")
						.build());
				String message = "hello";
				// 如果消费端消费异常导致没有回复转发成功，或者代码里写转发回复的相关代码，这边会报超时异常
				DeliveryOptions optionsTemp = new DeliveryOptions();
				optionsTemp.setSendTimeout(30 * 1000);
				vertx.get().eventBus().send("msg.test", message, optionsTemp, replyHandler -> {
					if (Objects.nonNull(replyHandler)) {
						if (replyHandler.succeeded()) {
							Message<Object> reply = replyHandler.result();
							if (Objects.nonNull(reply)) {
								String msg = (String) reply.body();
								printf(msg);
							}
						} else {
							replyHandler.cause().printStackTrace();
						}
					}
				});

				// 读取服务器的响应数据
				s.handler(buffer -> {
					printf(buffer.toString());
				});
				s.closeHandler(v -> {
					printf(getClientName() + "客户端关闭");
				});
			} else {
				printf("连接服务器异常");
			}
		});
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ClusterManager mgr = new ZookeeperClusterManager(curatorFramework);
		VertxOptions options = new VertxOptions().setClusterManager(mgr);
		options.getEventBusOptions()
				.setReceiveBufferSize(4194304)
				.setSendBufferSize(4194304);
		options.setClusterPort(8066);
		options.setClusterHost("192.168.2.173");
		MetricsOptions metrics = new MetricsOptions();
		options.setMetricsOptions(metrics);
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				vertx.set(res.result());
				vertx.get().deployVerticle(this);
				EventBus eventBus = vertx.get().eventBus();
				System.out.println("We now have a clustered event bus: " + eventBus);
			} else {
				System.out.println("Failed: " + res.cause());
			}
		});
	}

}
