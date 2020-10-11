package cn.wine.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author ChenJinKe
 */
@Component
public class ClientFactoryImpl implements InitializingBean, ClientFactory {
	@Resource
	List<Client> clients;

	private Map<String, Client> clientMap;


	@Override
	public void afterPropertiesSet() throws Exception {
		clientMap = clients.stream().collect(Collectors.toMap(Client::getClientName, Function.identity()));
	}

	@Override
	public Client getClientByName(String name) {
		return clientMap.get(name);
	}
}
