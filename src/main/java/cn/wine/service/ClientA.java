package cn.wine.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author ChenJinKe
 */
@Service
public class ClientA extends AbstractClient implements InitializingBean {

	@Override
	public String getClientName() {
		return "A";
	}


}
