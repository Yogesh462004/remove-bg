package in.yogesh.removebg.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;

import java.util.Map;

public interface RazorPayService {
    Order createOrder(Double amount, String currency)throws RazorpayException;
    Map<String ,Object> verifyPayement(String razorPayOrderId) throws RazorpayException;

}
