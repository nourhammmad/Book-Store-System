package com.book.store.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.book.store.Entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
@Entity
public class Order {

    public Order(Customer customer) {
        this.customer = customer;
    }
    public enum OrderStatus {
        PLACED, PROCESSING, COMPLETED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime orderDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)

    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        item.setOrder(this); // maintain bidirectional consistency
    }


    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    //  calculate total dynamically instead of just a field
    public float getTotalPrice() {
        if (items == null) return 0f;
        return (float) items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PLACED;

}
