package in.yogesh.removebg.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import in.yogesh.removebg.dto.UserDto;
import in.yogesh.removebg.entity.OrderEntity;
import in.yogesh.removebg.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RazorPayServiceImpl implements RazorPayService {
    @Value("${key.id}")
    private String razorpayKeyId;
    @Value("${key.secretid}")
    private String razorpayKeySecret;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Override
    public Order createOrder(Double amount, String currency) throws RazorpayException {
        try {
            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100); // in paise
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", "order_rcptid" + System.currentTimeMillis());
            orderRequest.put("payment_capture", 1); // Correct spelling
            return razorpayClient.orders.create(orderRequest);
        } catch (RazorpayException e) {
            e.printStackTrace();
            throw new RazorpayException("RazorPayError: " + e.getMessage());
        }
    }


    @Override
    public Map<String, Object> verifyPayement(String razorPayOrderId) throws RazorpayException {
        Map<String,Object> returnValue=new HashMap<>();

        try{
             RazorpayClient razorpayClient=new RazorpayClient(razorpayKeyId,razorpayKeySecret);
             Order orderInfo=razorpayClient.orders.fetch(razorPayOrderId);
             if(orderInfo.get("status").toString().equalsIgnoreCase("paid")){
                OrderEntity existingorder= orderRepository.findByOrderId(razorPayOrderId).orElseThrow(()->
                    new RuntimeException("Order Not Found for Order Id")
                );
                if(existingorder.getPayment()){
                    returnValue.put("success",false);
                    returnValue.put("message","Payement failed");
                    return  returnValue;
                }
                UserDto userDto=userService.getUserByClerId(existingorder.getClerkId());
                userDto.setCredits(userDto.getCredits()+existingorder.getCredits());
                userService.saveUser(userDto);
                existingorder.setPayment(true);
                orderRepository.save(existingorder);
                 returnValue.put("success",true);

                 returnValue.put("message","Credits Added");
                return  returnValue;

             }
         }catch (RazorpayException e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error While Verifying The payement");

        }
        return returnValue;
    }
}
