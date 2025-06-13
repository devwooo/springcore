package hello.core.service;

import hello.core.domain.Order;

public interface OrderService {
    Order createOrder(Long memberId, String itemName, int itemPrice);


}
