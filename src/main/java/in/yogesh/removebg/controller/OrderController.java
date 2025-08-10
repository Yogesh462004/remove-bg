package in.yogesh.removebg.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayException;
import in.yogesh.removebg.dto.RazorPayOrderDTO;
import in.yogesh.removebg.response.RemoveBgResponse;
import in.yogesh.removebg.service.OrderService;
import in.yogesh.removebg.service.RazorPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RazorPayService razorPayService;
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestParam String planId, Authentication authentication)throws RazorpayException {
        Map<String,Object> responseMap=new HashMap<>();
        RemoveBgResponse response=null;

            if(authentication.getName().isEmpty()||authentication.getName()==null){
                response=RemoveBgResponse.builder()
                        .statusCode(HttpStatus.FORBIDDEN)
                        .success(false)
                        .data("User does not have permission/access to this resource ")
                        .build();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            try{
                Order order=orderService.createOrder(planId,authentication.getName());
                RazorPayOrderDTO responsedto=convertToDto(order);
                response=RemoveBgResponse.builder().success(true).data(responsedto).statusCode(HttpStatus.CREATED).build();
                return ResponseEntity.ok(response);
            }catch (RazorpayException e){
                response=RemoveBgResponse.builder()
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                        .success(false)
                        .data(e.getMessage())
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
    }

    @PostMapping("/verifying")
    public ResponseEntity<?> verifyorder(@RequestBody Map<String,Object>request) throws RazorpayException{
        try{
            String razorpayOrderId=request.get("razorpay_order_id").toString();
            Map<String, Object> returnvalue = razorPayService.verifyPayement(razorpayOrderId);
            return ResponseEntity.ok(returnvalue);
        }catch (RazorpayException e){
            Map<String,Object> error=new HashMap<>();
            error.put("success",false);
            error.put("message",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    private RazorPayOrderDTO convertToDto(Order order) {
        return  RazorPayOrderDTO.builder()
                .id(order.get("id"))
                .entity(order.get("entity"))
                .amount(order.get("amount"))
                .currency(order.get("currency"))
                .status(order.get("status"))
                .created_At(order.get("created_at"))
                .receipt(order.get("receipt"))
                .build();
    }
}
