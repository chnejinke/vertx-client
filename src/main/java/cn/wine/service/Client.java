package cn.wine.service;

/**
 * @author ChenJinKe
 */
public interface Client {
	/**
	 * 获取客户端名称
	 *
	 * @return 客户端名称
	 */
	String getClientName();

	/**
	 * 发送消息
	 *
	 * @param msg 消息
	 */
	void sendMsg(MyMessage msg);

	/**
	 * 关闭
	 */
	void close();

	/**
	 * 启动
	 */
	void start();
}
