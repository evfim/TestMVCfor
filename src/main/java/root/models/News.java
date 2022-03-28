package root.models;

import javax.persistence.*;
import java.util.Date;

@Entity
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String text;
    private String hader;
    private Date date;


    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public News(long id, String text, String hader, Date date) {
        this.id = id;
        this.text = text;
        this.hader = hader;
        this.date = date;
    }

    public News(String text, String hader, Date date) {
        this.text = text;
        this.hader = hader;
        this.date = date;
    }

    public News(String text, String hader, Date date, User author, Product product) {
        this.text = text;
        this.hader = hader;
        this.date = date;
        this.author = author;
        this.product = product;
    }

    public News() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHader() {
        return hader;
    }

    public void setHader(String hader) {
        this.hader = hader;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", hader='" + hader + '\'' +
                ", date=" + date +
                '}';
    }
}
