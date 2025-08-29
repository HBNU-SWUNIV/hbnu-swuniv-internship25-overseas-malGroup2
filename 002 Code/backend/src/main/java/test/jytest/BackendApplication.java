package test.jytest;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // test.jytest 및 하위 패키지 전부 스캔
public class BackendApplication {

	public static void main(String[] args) {
		try{
			SpringApplication.run(BackendApplication.class, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
