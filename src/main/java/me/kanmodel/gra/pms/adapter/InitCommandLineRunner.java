package me.kanmodel.gra.pms.adapter;

import me.kanmodel.gra.pms.dao.OptionRepository;
import me.kanmodel.gra.pms.dao.RoleRepository;
import me.kanmodel.gra.pms.dao.UserRepository;
import me.kanmodel.gra.pms.entity.Option;
import me.kanmodel.gra.pms.entity.Role;
import me.kanmodel.gra.pms.entity.SysRole;
import me.kanmodel.gra.pms.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static sun.plugin.javascript.navig.JSType.Option;

/**
 * @description: 项目启动时运行 添加options Role的初始化数据
 * @author: KanModel
 * @create: 2019-07-09 09:05
 */
@Component
public class InitCommandLineRunner implements CommandLineRunner {

    @Value("${me.kanmodel.first}")
    private Boolean isFirst;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OptionRepository optionRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("...init resources by implements CommandLineRunner");

        if(isFirst && !optionRepository.findByKey("site_name").isPresent()){
            ArrayList<me.kanmodel.gra.pms.entity.Option> options = new ArrayList<>();
            options.add(new Option("site_name", "PMS", "网站名称"));
            options.add(new Option("fee_per_hours", "10", "每小时停车费"));
            options.add(new Option("free_hours", "1", "免费停车时间"));
            options.add(new Option("default_password", "123", "默认用户密码"));
            optionRepository.saveAll(options);
        }

        if (isFirst && !userRepository.findByLogin("admin").isPresent()) {
            ArrayList<SysRole> roles = new ArrayList<>();
            roles.add(new SysRole(Role.ROLE_USER));
            roles.add(new SysRole(Role.ROLE_ADMIN));
            roles.add(new SysRole(Role.ROLE_SUPER));
            roleRepository.saveAll(roles);
//            User user = new User("admin", bCryptPasswordEncoder.encode("admin"), Role.ROLE_SUPER);
            User user = new User("admin", bCryptPasswordEncoder.encode("admin"), roles);
            userRepository.save(user);
        }

        System.out.println("...init completed !");
    }

}
