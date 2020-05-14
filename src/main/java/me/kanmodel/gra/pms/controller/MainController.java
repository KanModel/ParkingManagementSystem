package me.kanmodel.gra.pms.controller;

import lombok.Data;
import me.kanmodel.gra.pms.entity.ParkScatter;
import me.kanmodel.gra.pms.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接收访问管理页面时登录登出的请求
 */

@Controller
@ApiIgnore
public class MainController {

    @ModelAttribute
    public void generalModel(Model model) {
        model.addAttribute("site_name", "PMS");
    }

    @RequestMapping(value = {"/index", "/"})
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/about")
    public String about() {return "about"; }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(Model model,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         RedirectAttributes attr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("ID:" + ((User) auth.getPrincipal()).getId() + " Logout");
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        attr.addFlashAttribute("logout", true);
        return "redirect:/login";
    }

    @RequestMapping("/passerror")
    public String loginError(Model model,
                             RedirectAttributes attr) {
        attr.addFlashAttribute("passerror", true);
        return "redirect:/login";
    }

    @RequestMapping("/space")
    public String home(Model model,
                       @RequestParam(value = "res", required = false) String res) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
        model.addAttribute("res", res);
        return "space";
    }
}
