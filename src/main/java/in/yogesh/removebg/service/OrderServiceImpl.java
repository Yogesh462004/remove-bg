package in.yogesh.removebg.service;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import in.yogesh.removebg.entity.OrderEntity;
import in.yogesh.removebg.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    @Autowired
    private RazorPayService razorPayService;
    @Autowired
    private OrderRepository orderRepository;
    private static final Map<String,PlanDetails> PLAN_DETAILS=Map.of(
            "Basic",new PlanDetails("Basic",10,10),
            "Premium",new PlanDetails("Premium",30,25),
            "Ultimate",new PlanDetails("Ultimate",50,45)
    );
    private record PlanDetails(String name,int credits,double amount){

    }
    @Override
    public Order createOrder(String palnid, String clerkId) throws RazorpayException {
        PlanDetails details=PLAN_DETAILS.get(palnid);
        if(details==null){
            throw new IllegalArgumentException("Invalid planid");

        }
        try{
            Order razorpayorder=razorPayService.createOrder(details.amount(),"INR");
           OrderEntity newOrder= OrderEntity.builder()
                    .clerkId(clerkId)
                    .plan(details.name())
                    .credits(details.credits())
                    .amount(details.amount())
                    .orderId(razorpayorder.get("id"))
                    .build();
           orderRepository.save(newOrder);
           return razorpayorder;
        }catch (Exception e){
            throw new RazorpayException("Error While Creating the Order");
        }
    }
}
