package me.kanmodel.gra.pms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class ParkingManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParkingManagementSystemApplication.class, args);
    }

}
