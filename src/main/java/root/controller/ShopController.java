package root.controller;


import root.models.*;
import root.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class ShopController {
    @Autowired
    private ProductTypeRepository productTypeRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItem2Repository cartItem2Repository;
    @Autowired
    private RatingRepository ratingRepository;
    //Открытие формы магазина
    @GetMapping("/shop")
    public String showProducts(Model model) {
        Iterable<Product> t = productRepository.findAll();
        model.addAttribute("types", t);
        return "mainPages/shop";
    }

    /////////////////////////////////////Подробнее о товре и корзина
    @GetMapping ("/detailsProduct")
    public  String detailsProduct(@RequestParam(name="id", required = false) String id,
                                  Model model){
        Product product = productRepository.findById(Long.parseLong(id)).orElse(null);
        List<Product> t = new ArrayList<>();
        t.add(product);
        model.addAttribute("list", t);
        //Чтобы сразу показывать кол-во товаров в корзине
        CartItem cart = cartItemRepository.findByProduct(product);
        if (cart != null){
            model.addAttribute("howmany", cart.getQuantity());
        }
       List<News> news = newsRepository.findByProduct(product);
       model.addAttribute("list2", news);
       List<Rating> ioi = ratingRepository.findByProduct(product);
       if(ioi.size() > 0){
           int sum = 0;
           int lnth = ioi.size();
           for(int i =0; i<ioi.size();i++){
               sum = ioi.get(i).getNumber() + sum;
           }
           int number = sum/lnth;
           String words = number + "";
           model.addAttribute("newWords", words);
        }else{
           model.addAttribute("newWords", "нет рейтинга" );
        }
        return "mainPages/detailsAboutProduct";
    }

   @PostMapping("/updateShoppingCart")
    public  String updateShoppingCart( @AuthenticationPrincipal User user,
                                       @RequestParam(name="id", required = false) String id,
                                       @RequestParam(name="value", required = false) int value,
                                       Model model){
       //Проверка есть ли такой продукт в корзине
       Product prod = productRepository.findById(Long.parseLong(id)).orElse(null);
       CartItem cart = cartItemRepository.findByProduct(prod);
       if (cart != null){
           if(value <= 0){
               cartItemRepository.delete(cart);
           }else{
               cart.setQuantity(value);
               cartItemRepository.save(cart);
           }
       }else{
           if(value > 0){
               CartItem newItem = new CartItem();
               newItem.setUser(user);
               newItem.setProduct(prod);
               newItem.setQuantity(value);
               cartItemRepository.save(newItem);
           }
       }
        return "redirect:/cart";
    }
    ///////////////////////////////////////КОРЗИНА
    @GetMapping("/cart")
    public String cart(@AuthenticationPrincipal User user,
                       Model model) {
        List<CartItem> cart = cartItemRepository.findByUser(user);
        List<Product> t = new ArrayList<>();
        for(int i = 0; i < cart.size(); i++){
            for(int j = 0; j < cart.get(i).getQuantity(); j++){
                t.add(cart.get(i).getProduct());
            }
        }

        model.addAttribute("types", t);
        return "mainPages/cart";
    }
   //Удаление всей корзины
    @PostMapping("/deleteAllCart")
    public  String deleteAllCart(@AuthenticationPrincipal User user,
                                 Model model){
        List<CartItem> cart = cartItemRepository.findByUser(user);
        for(int i = 0; i < cart.size(); i++){
            cartItemRepository.delete(cart.get(i));
        }
        return "redirect:/shop";
    }
    //Отзыв о товаре
    @PostMapping("/feedback")
    public String feedback(@AuthenticationPrincipal User user,
                           @RequestParam(name="text", required = false, defaultValue = "no") String text,
                           @RequestParam(name="hader", required = false, defaultValue = "no") String hader,
                           @RequestParam(name="id2", required = false) String id2,
                           Model model){
        News n = new News();
        n.setText(text);
        n.setHader(hader);
        n.setDate(new Date());
        n.setAuthor(user);
        Product prod = productRepository.findById(Long.parseLong(id2)).orElse(null);
        n.setProduct(prod);
        newsRepository.save(n);

        List<News> news = newsRepository.findByProduct(prod);
        model.addAttribute("list2", news);

        return "redirect:/cart";
    }
    ///////////Заказ товаров
    @PostMapping("/order")
    public  String order(Model model){
        return "allAboutOrders/mainOrder";
    }
    //Добавление информации для заказа и составление заказа
    @PostMapping("/additionalRegistration")
    public String additionalRegistration(@AuthenticationPrincipal User user,
            @RequestParam(name="mobile", required=false, defaultValue="не указан") String mobile,
            @RequestParam(name="payment", required=false, defaultValue="не указан") String payment,
            @RequestParam(name="delivery", required=false, defaultValue="не указан") String delivery,
            Model model
    ){
        Orders orders = new Orders(); //Добавление в заказ
        orders.setUser(user);
        userRepository.save(user);
        List<CartItem> li = cartItemRepository.findByUser(user);
        //Сохранение заказов во второй корзине (для истории и чтобы у пользователя было больше не видно) и удаление из первой корзины
        orders.setMobile(mobile);
        orders.setPayment(payment);
        orders.setDelivery(delivery);
        orders.setDate(new Date());
        orderRepository.save(orders);

        for(int i = 0; i < li.size(); i++){
            CartItem2 cart = new CartItem2();
            cart.setProduct(li.get(i).getProduct());
            cart.setQuantity(li.get(i).getQuantity());
            cart.setUser(user);
            cart.setOrders(orders);
            cartItem2Repository.save(cart);
            cartItemRepository.delete(li.get(i));
        }

        return "allAboutOrders/finished";
    }
    //////////////////////////////////////////////Оценка
    @PostMapping("/productEvaluation")
    public  String productEvaluation(@AuthenticationPrincipal User user,
                                     @RequestParam(name="chooseRating", required = false, defaultValue = "1") String chooseRating,
                                     @RequestParam(name="id3", required = false) String id3,
                                     Model model){
        Rating rating = ratingRepository.findByUser(user);
        Product prod = productRepository.findById(Long.parseLong(id3)).orElse(null);

        if(rating == null){
            Rating r = new Rating();
            r.setProduct(prod);
            r.setNumber(Integer.parseInt(chooseRating));
            ratingRepository.save(r);

        }else{
            rating.setNumber(Integer.parseInt(chooseRating));
            ratingRepository.save(rating);
        }
        return "mainPages/detailsAboutProduct";
    }
}
