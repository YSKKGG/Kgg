import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author kgg
 * @date 2023/12/19
 */
@SpringBootApplication(scanBasePackages = {"com.kgg.kkchat"})
public class KkchatCustomApplication {

    public static void main(String[] args) {
        SpringApplication.run(KkchatCustomApplication.class,args);
    }

}