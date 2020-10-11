package cn.wine.service;

/**
 * @author ChenJinKe
 */
public interface ClientFactory {
	/**
	 * 根据客户端名字获取客户端服务
	 *
	 * @param name 客户端名字
	 * @return 客户端服务
	 */
	Client getClientByName(String name);

}
