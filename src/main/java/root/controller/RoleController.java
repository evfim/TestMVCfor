package root.controller;

import root.models.*;
import root.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAnyAuthority('Admin')") //Позволяет зайти только пользоватлею с ролью Admin-а
public class RoleController {
    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItem2Repository cartItem2Repository;

    @GetMapping
    public String userList(Model model){
        Iterable<User> y = userRepository.findAll();
        model.addAttribute("list", y);
        return "onlyAdminOptions/userList";
    }

  @GetMapping ("/intform")
    public  String viewUser(@RequestParam(name="id", required=false) String id, Model model){
      User g = userRepository.findById(Long.parseLong(id)).orElse(null);
        List<User> t = new ArrayList<>();
        t.add(g);
        model.addAttribute("eamil", g.getEmail());
        model.addAttribute("list", t);

        if (g.getRoles().size() >= 2 ){
            model.addAttribute("rolesEdit", "yes");
        }else{
            model.addAttribute("rolesEdit", "no");
        }

        return "editOptions/updatePage";
        }

    @PostMapping("/updateUser")
    public  String editUserById(@RequestParam(name="id", required=false) String id,
                                @RequestParam(name="username", required=false, defaultValue="World") String username,
                                @RequestParam(name="email", required=false, defaultValue="World") String email,
                                @RequestParam(name="password", required=false, defaultValue="World") String password,
                                @RequestParam(name="rolesEdit", required=false, defaultValue="World") String rolesEdit,
                                Model model){
        User g = userRepository.findById(Long.parseLong(id)).orElse(null);
        g.setUsername(username);
        g.setEmail(email);
        g.setPassword(password);

        if(rolesEdit.equals("yes")){
            Set<Role> r = new HashSet<>();
            r.add(Role.Admin);
            r.add(Role.User);
            g.setRoles(r);
        }else{
            Set<Role> r = new HashSet<>();
            r.add(Role.User);
            g.setRoles(r);
        }
        userRepository.save(g);
        return "redirect:/user";
    }
    //Доавление товара
    @GetMapping("/addProduct")
    public String addProduct(Model model) {
        Iterable<Product> t = productRepository.findAll();
        model.addAttribute("types", t);
        Iterable<ProductType> t1 = productTypeRepository.findAll();
        model.addAttribute("list1", t1);
        Iterable<Manufacturer> t2 = manufacturerRepository.findAll();
        model.addAttribute("list2", t2);
        return "addOptions/addProduct";
    }

    @PostMapping("/addProductById")
    public String addProductById(
            @RequestParam(name="name", required=false, defaultValue="не указан") String name,
            @RequestParam(name="price", required=false, defaultValue="не указан") int price,
            @RequestParam(name="description", required=false, defaultValue="не указан") String description,
            @RequestParam(name="typeId", required=false, defaultValue="не указан") String typeId,
            @RequestParam(name="manufacturerId", required=false, defaultValue="не указан") String manufacturerId,
            Model model
    ){
        Product product1 = new Product();
        product1.setName(name);
        product1.setPrice(price);
        product1.setDescription(description);
        ProductType productType = productTypeRepository.findById(Long.parseLong(typeId)).orElse(null);
        product1.setProductType(productType);
        Manufacturer mn = manufacturerRepository.findById(Long.parseLong(manufacturerId)).orElse(null);
        product1.setManufacturer(mn);
        productRepository.save(product1);
        return "redirect:/user/addProduct";
    }
    //Добавление категории товара
    @GetMapping("/typeProduct")
    public String showProductTypeList(Model model) {
        Iterable<ProductType> t = productTypeRepository.findAll();
        model.addAttribute("list", t);
        return "addOptions/typeProduct";
    }

    @PostMapping("/createProductType")
    public String createProductType(
            @RequestParam(name="name", required=false, defaultValue="не указан") String name,
            Model model
    ){
        ProductType pr = new ProductType();
        pr.setName(name);
        productTypeRepository.save(pr);
        return "redirect:/user/typeProduct";
    }
    //Добавление производителя
    @GetMapping("/addManufact")
    public String showProductManufactureList(Model model) {
        Iterable<Manufacturer> t = manufacturerRepository.findAll();
        model.addAttribute("list", t);
        return "addOptions/addManufact";
    }

    @PostMapping("/createManufactureType")
    public String createManufactureType(
            @RequestParam(name="name", required=false, defaultValue="не указан") String name,
            Model model
    ){
        Manufacturer pr = new Manufacturer();
        pr.setDateOfreg(new Date());
        pr.setName(name);
        manufacturerRepository.save(pr);
        return "redirect:/user/addManufact";
    }
    ///////////////////////////////////////////////////Редактирование информации
    //Производителя
    @PostMapping("/editManufactureById")
    public  String editManufactureById(@RequestParam(name="numbform", required = false, defaultValue = "no") String numbform,
                                       Model model){
        Manufacturer g = manufacturerRepository.findById(Long.parseLong(numbform)).orElse(null);
        List<Manufacturer> t = new ArrayList<>();
        t.add(g);
        model.addAttribute("list", t);
        return "editOptions/editManufact";
    }
    @PostMapping("/editManufacture")
    public  String editManufacture(@RequestParam(name="id", required=false, defaultValue="не указан") String id,
                                   @RequestParam(name="name", required=false, defaultValue="не указан") String name,
                                   Model model){
        Manufacturer manufacturer = manufacturerRepository.findById(Long.parseLong(id)).orElse(null);
        manufacturer.setName(name);
        manufacturerRepository.save(manufacturer);
        return "redirect:/user/addManufact";
    }
    @PostMapping("/deleteManufactureById")
    public  String deleteManufactureById(@RequestParam(name="numbform", required = false, defaultValue = "no") String numbform,
                                         Model model){
        Manufacturer manufacturer = manufacturerRepository.findById(Long.parseLong(numbform)).orElse(null);
        List<Product> t = productRepository.findByManufacturer(manufacturer);
        Manufacturer m = null;
        if(manufacturerRepository.findByName("не указан") == null){
           m = new Manufacturer("не указан");
           manufacturerRepository.save(m);
        }else{
            m = manufacturerRepository.findByName("не указан");
        }
        for (int i = 0; i < t.size(); i++){
            t.get(i).setManufacturer(m);
            productRepository.save(t.get(i));
        }
        manufacturerRepository.delete(manufacturer);
        return "redirect:/user/addManufact";
    }
    //Категории
    @PostMapping("/editProductTypeById")
    public  String editProductTypeById(@RequestParam(name="numbform", required = false) String numbform,
                                       Model model){
        ProductType productType = productTypeRepository.findById(Long.parseLong(numbform)).orElse(null);
        List<ProductType> t = new ArrayList<>();
        t.add(productType);
        model.addAttribute("list", t);
        return "editOptions/editCat";
    }

    @PostMapping("/editProductType")
    public  String editProductType(@RequestParam(name="id", required=false) String id,
                                   @RequestParam(name="name", required=false, defaultValue="no") String name,
                                   Model model){
        ProductType productTypeMore = productTypeRepository.findById(Long.parseLong(id)).orElse(null);
        productTypeMore.setName(name);
        productTypeRepository.save(productTypeMore);
        return "redirect:/user/typeProduct";
    }

    @PostMapping("/deleteProductType")
    public  String deleteProductType(@RequestParam(name="numbform", required = false) String numbform,
                                     Model model){
        ProductType productType = productTypeRepository.findById(Long.parseLong(numbform)).orElse(null);
        List<Product> t = productRepository.findByProductType(productType);
        ProductType p = null;
        if(productTypeRepository.findByName("не указан") == null){
            p = new ProductType("не указан");
            productTypeRepository.save(p);
        }else{
            p = productTypeRepository.findByName("не указан");
        }
        for (int i = 0; i < t.size(); i++){
            t.get(i).setProductType(p);
            productRepository.save(t.get(i));
        }
        productTypeRepository.delete(productType);
        return "redirect:/user/typeProduct";
    }
    //Продукт
    @PostMapping("/editProductById")
    public  String editProductById(@RequestParam(name="numbform", required = false) String numbform,
                                   Model model){
        Product product = productRepository.findById(Long.parseLong(numbform)).orElse(null);
        List<Product> t = new ArrayList<>();
        t.add(product);
        model.addAttribute("list", t);
        return "editOptions/editProduct";
    }

    @PostMapping("/editProduct")
    public  String editProduct(@RequestParam(name="id", required=false) String id,
                               @RequestParam(name="name", required=false, defaultValue="не указан") String name,
                               @RequestParam(name="description", required=false, defaultValue="не указан") String description,
                               @RequestParam(name="price", required=false, defaultValue="не указан") int price,
                               Model model){
        Product product = productRepository.findById(Long.parseLong(id)).orElse(null);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        productRepository.save(product);
        return "redirect:/user/addProduct";
    }

    @PostMapping("/deleteProduct")
    public  String deleteProduct(@RequestParam(name="numbform", required = false, defaultValue = "no") String numbform,
                                 Model model){
        Product product = productRepository.findById(Long.parseLong(numbform)).orElse(null);
        product.setProductType(null);
        product.setManufacturer(null);
        productRepository.save(product);
        productRepository.delete(product);
        return "redirect:/user/addProduct";
    }
    //Все заказы
    @GetMapping("/allOrders")
    public String allOrders(Model model){
        List<Orders> ordersList = orderRepository.findAll();
        model.addAttribute("list", ordersList);
        return "allAboutOrders/allOrders";
    }
    //Подробнее о заказе
    @PostMapping("/ordersDetails")
    public  String ordersDetails(@RequestParam(name="id", required = false, defaultValue = "no") String id,
                                 Model model){
        Orders y1 = orderRepository.findById(Long.parseLong(id)).orElse(null);
        List<Orders> y = new ArrayList<>();
        y.add(y1);
        model.addAttribute("list", y);

        List<User> userList = new ArrayList<>();
        userList.add(y.get(0).getUser());
        model.addAttribute("list2", userList);

        List<CartItem2> g = cartItem2Repository.findByOrders(y.get(0));
        model.addAttribute("list3", g);

        return "allAboutOrders/ordersDetails";
    }
}

