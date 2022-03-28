package root.controller;

import root.models.Role;
import root.models.User;
import root.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.TreeSet;

@Controller
public class MainController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String registration(){
        return "accesResource/registration";
    }

    @PostMapping("/registration")
    public String addUser(
            @RequestParam(name="username", required=false, defaultValue="не указан") String username,
            @RequestParam(name="email", required=false, defaultValue="не указан") String email,
            @RequestParam(name="password", required=false, defaultValue="не указан") String password,
            Model model
    ){
        boolean g1 = username.matches("(?i)(^[a-z])((?![ .,'-]$)[a-z .,'-]){0,24}$");
        boolean g2 = email.matches("\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*\\.\\w{2,4}");
        boolean g3 =  password.matches("\\b[A-Za-z0-9.-]{6,20}\\b");
        User b1 = userRepository.findByUsername(username);
        User b2 = userRepository.findByEmail(email);
        boolean g4 = b1 == null;
        boolean g5 = b2 == null;

        if(g1 && g2 && g3 && g4 && g5){
            //Если верно введены данные
            root.models.User u = new root.models.User(username, passwordEncoder.encode(password), true, email);
            TreeSet<Role> list = new TreeSet<>();
            list.add(Role.User);
            //list.add(Role.Admin);
            u.setRoles(list);
            userRepository.save(u);
        } else{
            //Если данные введены не верно
            if(g1 == false){
                model.addAttribute("dataErrorUser", "Поле имя заполненно не верно");
            }else if(g2 == false){
                model.addAttribute("dataErrorUser", "Поле почта заполненно не верно");
            }else if(g3 == false){
                model.addAttribute("dataErrorUser", "Поле пароль заполненно не верно");
            }else if(g4 == false){
                model.addAttribute("dataErrorUser", "Пользователь с таким именем уже существует");
            } else{
                model.addAttribute("dataErrorUser", "Пользователь с такой почтой уже существует");
            }
            return "errorOfRegistration/dataError";
        }
        return "/accesResource/login";
    }

    @GetMapping("/login")
    public String Login(){
        return "/accesResource/login";
    }
}
