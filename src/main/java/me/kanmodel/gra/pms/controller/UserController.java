package me.kanmodel.gra.pms.controller;

import me.kanmodel.gra.pms.dao.OptionRepository;
import me.kanmodel.gra.pms.dao.RoleRepository;
import me.kanmodel.gra.pms.dao.UserRepository;
import me.kanmodel.gra.pms.entity.SysRole;
import me.kanmodel.gra.pms.entity.User;
import me.kanmodel.gra.pms.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @description: 接收对用户增删改查的请求
 * @author: KanModel
 * @create: 2019-03-23 09:51
 */
@Controller
@ApiIgnore
public class UserController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private OptionRepository optionRepository;

    private static Sort sort = new Sort(Sort.Direction.ASC, "id");

    @ModelAttribute
    public void generalModel(Model model) {
        model.addAttribute("site_name", optionRepository.findByKey("site_name").get().getValue());
    }

    @RequestMapping("/user")
    public String user(Model model,
                       @RequestParam(value = "add_res", required = false) String add_res,
                       @RequestParam(value = "find_res", required = false) String find_res) {
        model.addAttribute("add_res", add_res).addAttribute("find_res", find_res);
        return "user/user";
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public User getUser(@PathVariable(name = "id") Long id) {
        Optional<User> tmp = userRepository.findById(id);
        User user = null;
        if (tmp.isPresent()) {
            user = tmp.get();
        }
        return user;
    }

    @RequestMapping("/user/list")
    public ModelAndView listUsers(@RequestParam(value = "res", required = false) String res,
                                  @RequestParam(value = "login", required = false, defaultValue = "") String login,
                                  @RequestParam(value = "no", defaultValue = "1", required = false) int pageNo,
                                  @RequestParam(value = "size", defaultValue = "10", required = false) int pageSize) {
        System.out.println("PageNo" + pageNo + " PageSize" + pageSize + login);
        Page<User> page;
        if (login != null && !login.equals("")) {
            page = userService.selectAllByLogin(pageNo, pageSize, login);
        } else {
            page = userService.selectAll(pageNo, pageSize);
        }
        List<User> list = page.getContent();
        int pageCount = page.getTotalPages();
        ModelAndView modelAndView = new ModelAndView("/user/user_list");
        modelAndView.addObject("userList", list);
        modelAndView.addObject("res", res);
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("pageCount", pageCount);
        if (login != null) modelAndView.addObject("login", login);
        return modelAndView;
    }

    /**
     * 添加用户
     *
     * @param login 登录用户名
     * @param pass  登录密码
     * @return
     */
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public String userAdd(@RequestParam(value = "login", required = false) String login,
                          @RequestParam(value = "pass", required = false) String pass,
                          Model model) {
        if (!userRepository.findByLogin(login).isPresent()) {
            User user = new User(login, bCryptPasswordEncoder.encode(pass));
            userRepository.save(user);
            model.addAttribute("add_res", "添加用户[" + login + "]成功！");
            System.out.println("Add user [" + login + "] : " + pass);
        } else {
            model.addAttribute("add_res", "用户[" + login + "]已存在！");
            System.out.println("User[" + login + "] is existed!");
        }
        return "user/user";
    }

    /**
     * 随机生成用户
     *
     * @param count
     * @param pass
     * @param model
     * @return
     */
    @RequestMapping(value = "/user/randomadd", method = RequestMethod.POST)
    public String randomUserGenerate(@RequestParam(value = "count", required = false, defaultValue = "10") int count,
                                     @RequestParam(value = "pass", required = false, defaultValue = "123") String pass,
                                     Model model) {
        String password = pass;
        if (password.equals("123")) password = optionRepository.findByKey("default_password").get().getValue();
        logger.info("Generate count:" + count + " default Password:" + password);
        String res = "添加用户：";
        for (int i = 0; i < count; i++) {
            String login = getRandomString(5);
            if (!userRepository.findByLogin(login).isPresent()) {
                User user = new User(login, bCryptPasswordEncoder.encode(password));
                userRepository.save(user);
                model.addAttribute("add_res", "添加用户[" + login + "]成功！");
                res += " " + login;
            } else {
                model.addAttribute("add_res", "用户[" + login + "]已存在！");
            }
        }
        model.addAttribute("add_res", res);
        return "user/user";
    }

    private static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @RequestMapping(value = "/user/find", method = RequestMethod.POST)
    public ModelAndView findUser(@RequestParam(value = "id", required = false) String id,
                                 @RequestParam(value = "name", required = false) String name) {
        ModelAndView modelAndView = new ModelAndView("redirect:/user");
        Optional<User> user;
        System.out.println("Find user id: " + id + ", name: " + name);
        //id非数字
        try {
            if (!id.equals("")) user = userRepository.findById(Long.valueOf(id));
            else {
                if (name.equals("")) user = Optional.empty();
                else user = userRepository.findByLogin(name);
            }
        } catch (NumberFormatException e) {
            return modelAndView.addObject("find_res", "id应该为数字！");
        }

        if (user.isPresent()) {
            return modelAndView
                    .addObject("find_res", "查找结果:" + user.get().getLogin());
        } else {
            return modelAndView.addObject("find_res", "查找失败！");
        }
    }

    @RequestMapping(value = "/user/del", method = RequestMethod.POST)
    public ModelAndView deleteUser(@RequestParam(value = "id", required = false) String id,
                                   @RequestParam(value = "no", defaultValue = "1", required = false) int pageNo) {
        System.out.println("Userdel PageNo" + pageNo);
        User deleteUser = userRepository.findById(Long.valueOf(id)).get();
        ModelAndView modelAndView = new ModelAndView("redirect:/user/list");
        if (deleteUser.getRoles().size() > 1) {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            boolean isSuper = false;
            for (SysRole sysRole : user.getRoles()) if (sysRole.getName().equals("ROLE_SUPER")) isSuper = true;
            if (!isSuper) {
                modelAndView.addObject("no", pageNo);
                modelAndView.addObject("res", "您无权删除权限大于等于您的用户!");
                return modelAndView;
            }
        }
        deleteUser.setIsDelete(true);
        userRepository.save(deleteUser);
        modelAndView.addObject("no", pageNo);
        modelAndView.addObject("res", "成功删除id为" + id + "的用户");
        return modelAndView;
    }

    @RequestMapping(value = "/user/edit/pass", method = RequestMethod.POST)
    public ModelAndView editUserPass(@RequestParam(value = "id", required = false) String id,
                                     @RequestParam(value = "no", defaultValue = "1", required = false) int pageNo,
                                     @RequestParam(value = "pass", required = false, defaultValue = "123") String pass) {
        Optional<User> optionalUser = userRepository.findById(Long.valueOf(id));
        ModelAndView modelAndView = new ModelAndView("redirect:/user/list");
        if (optionalUser.isPresent()) {
            User target = optionalUser.get();
            if (target.getRoles().size() > 1) {
                boolean isSuper = false;
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                for (SysRole sysRole : user.getRoles()) if (sysRole.getName().equals("ROLE_SUPER")) isSuper = true;
                if (!isSuper) {
                    modelAndView.addObject("no", pageNo);
                    modelAndView.addObject("res", "您无权为权限大于等于您的用户修改密码!");
                    return modelAndView;
                }
            }
            target.setPass(bCryptPasswordEncoder.encode(pass));
            userRepository.save(target);
            modelAndView.addObject("no", pageNo);
            modelAndView.addObject("res", "成功为id为" + id + "的用户修改密码");
        }
        return modelAndView;
    }

    @RequestMapping("/user/edit")
    public String edit() {
        return "user/user_edit";
    }

    @RequestMapping("/user/edit/password")
    public String editPassword() {
        return "user/user_edit_password";
    }

    @RequestMapping("/user/edit/info")
    public String editDisplay() {
        return "user/user_edit_info";
    }

    @RequestMapping(value = "/user/edit/mpass", method = RequestMethod.POST)
    public String editUserSelfPass(@RequestParam(value = "old_pass") String oldPass,
                                   @RequestParam(value = "new_pass") String newPass,
                                   Model model) {
        User sUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(sUser.getId()).get();
        if (bCryptPasswordEncoder.matches(oldPass, user.getPassword())) {
            user.setPass(bCryptPasswordEncoder.encode(newPass));
//            modelAndView.addObject("res", "成功更改密码!");
            model.addAttribute("res", "成功更改密码!");
            userRepository.save(user);
        } else {
            model.addAttribute("res", "密码错误!");
        }
        return "user/user_edit_password";
    }

    @RequestMapping(value = "/user/edit/display", method = RequestMethod.POST)
    public String editUserDisplay(@RequestParam(value = "display", required = false) String display,
                                  Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user.setDisplay(display);
        userRepository.save(user);
        model.addAttribute("res", "更改显示名为" + display + "!");
        return "user/user_edit_info";
    }

    @RequestMapping(value = "/user/edit/email", method = RequestMethod.POST)
    public String editUserEmail(@RequestParam(value = "email", required = false) String email,
                                  Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        if (!regex.matcher(email).matches()){
            model.addAttribute("res", "邮箱格式不正确!");
            return "user/user_edit_info";
        }
        user.setEmail(email);
        userRepository.save(user);
        model.addAttribute("res", "更改用户邮箱为" + email + "!");
        return "user/user_edit_info";
    }

    @RequestMapping(value = "/user/edit/role", method = RequestMethod.POST)
    public ModelAndView editUserRole(@RequestParam(value = "id", required = false) Long id,
                                     @RequestParam(value = "no", defaultValue = "1", required = false) int pageNo,
                                     @RequestParam(value = "role", required = false) List<String> roles) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User now = user.get();
            List<SysRole> newRoles = new ArrayList<>();
            for (String role : roles) {
                newRoles.add(roleRepository.findByName(role));
            }
            now.setRoles(newRoles);
            userRepository.save(now);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (now.getId().equals(((User) auth.getPrincipal()).getId())) {
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), now.getAuthorities()));
            }
        }
        ModelAndView modelAndView = new ModelAndView("redirect:/user/list");
        modelAndView.addObject("no", pageNo);
        modelAndView.addObject("res", "成功为id为" + id + "的用户修改权限");
        return modelAndView;
    }
}
