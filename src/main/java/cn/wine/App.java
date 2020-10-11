package cn.wine;

import cn.wine.service.ClientFactory;
import cn.wine.service.MyMessage;
import javax.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ChenJinKe
 */
@RestController
@SpringBootApplication
public class App {

	@Resource
	private ClientFactory clientFactory;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@PostMapping("/sendMsg")
	public String sendMsg(@RequestBody MyMessage message) {
		clientFactory.getClientByName(message.getClientId()).sendMsg(message);
		return "success";
	}

	@GetMapping("close")
	public String close(@RequestParam String clientName) {
		clientFactory.getClientByName(clientName).close();
		return "success";
	}

	@GetMapping("start")
	public String start(@RequestParam String clientName) {
		clientFactory.getClientByName(clientName).start();
		return "success";
	}
}
