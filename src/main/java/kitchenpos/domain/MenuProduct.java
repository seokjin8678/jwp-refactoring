package kitchenpos.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class MenuProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false)
    private long quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Menu menu;

    public MenuProduct() {
    }

    public MenuProduct(Long seq, long quantity, Product product, Menu menu) {
        this.seq = seq;
        this.quantity = quantity;
        this.product = product;
        this.menu = menu;
    }

    public Money getTotalPrice() {
        return product.getPrice().multiple(quantity);
    }

    public Long getSeq() {
        return seq;
    }

    public long getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    public Menu getMenu() {
        return menu;
    }
}
