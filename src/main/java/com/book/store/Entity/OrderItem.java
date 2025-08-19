package com.book.store.Entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
//for extra safety measures
@ToString(exclude = {"order", "book"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class OrderItem {
    @Builder
    public OrderItem(Order order, Book book, int quantity) {
        this.order = order;
        this.book = book;
        this.quantity = quantity;
        //do not pass price manually
        this.price = book.getPrice() * quantity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private int quantity;
    private float price;

}
