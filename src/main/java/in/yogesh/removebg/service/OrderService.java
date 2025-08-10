package in.yogesh.removebg.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface OrderService {
    Order createOrder(String palnid, String clerkId) throws RazorpayException;
}
