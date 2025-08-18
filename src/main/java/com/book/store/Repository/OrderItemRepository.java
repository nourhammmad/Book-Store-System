package com.book.store.Repository;

import com.book.store.Entity.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;

//note this might be needed if i want to implement these features:
//Find all OrderItems for a specific book.
//Get sales statistics (sum(quantity) of a certain book across all orders).
//Fetch all items of an order without loading the whole order object.
@Repository
public interface OrderItemRepository {
    List<OrderItem> findByBookId(Integer bookId);
    List<OrderItem> findByOrderId(Integer orderId);

}
