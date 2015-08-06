package models;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.*;
import play.data.validation.Constraints;
//DB Imports
import javax.persistence.*;
import play.db.ebean.*;
import javax.persistence.ManyToOne;

@Entity
public class ShoppingCart extends Model{
    
    public static Finder<Long, ShoppingCart> find = new Finder<Long, ShoppingCart>(Long.class, ShoppingCart.class);
    
    
@Id
    public Long shoppingcart_id;
         //@Constraints.Required
//@ManyToOne
    public Long cust_id;  
    public Date date_made = new Date(); 
    public Date date_shipped;
    public double total;
    public double shipping_cost;
    public double subtotal;
    public String transaction_status = "Open";
    public String billing_address1;
    public String billing_address2;
    public String billing_city;
    public String billing_country;
		 
public ShoppingCart(){}
		  
    public void setSubtotal(double total){
        this.subtotal= total;
    }
    
    public void setCustId(Long id){
        this.cust_id = id;
    }
    
    public static Long returnOpenCart(Long cid){       
    ShoppingCart sc = find.where().eq("cust_id", cid).eq("transaction_status", "Open").findUnique();    
    return sc.shoppingcart_id;
    }
    
    public static Long returnCart(Long cid){       
    ShoppingCart sc = find.where().eq("cust_id", cid).findUnique();    
    return sc.shoppingcart_id;
    }
    
    public static void cancelCart(Long cartId){
        ShoppingCart sc = returnByCartId(cartId);
        sc.total = 0;
        sc.shipping_cost = 0;
        sc.calcSubtotal();
        sc.transaction_status = "Cancelled";
        sc.save();
    }
                
    
    
    
    public static List<ShoppingCart> returnHistory(Long cid){
        return find.where().eq("cust_id", cid).findList(); 
    }
    
    public static ShoppingCart returnCartObject(Long cid){ 
        return find.where().eq("cust_id", cid).eq("transaction_status", "Open").findUnique(); 
    }
    
    public static ShoppingCart returnByCartId(Long cid){ 
        return find.where().eq("shoppingcart_id", cid).findUnique(); 
    }
    
    public void setDefaultBillingAddress(String a1, String a2, String billCity,String billCountry){
    billing_address1 = a1;
    billing_address2 = a2;
    billing_city = billCity;
    billing_country = billCountry;            
    }
       
    
    public void calcOrderTotal(Long cartId){
        List<OrderDetails> list = OrderDetails.getOrderDetailList(cartId);
        double total = 0;
        for(int i = 0; i < list.size(); i++){
            total += list.get(i).line_total;
    }
        this.total = total;        
    }
    
    public void calcShippingCost(){
        if(billing_country.equalsIgnoreCase("IRELAND")){
            shipping_cost = 4.99;
        }
        else{
            shipping_cost = 9.99;
    }
    }  
    
    public void calcSubtotal(){
        subtotal = total + shipping_cost;
    }
    
    public void changeStatus(String newStatus){
        transaction_status = newStatus;
    }
    
    public static List<ShoppingCart> findAll() {
                    return ShoppingCart.find.all();
                 }
    
    public static List<ShoppingCart> byStatus(String status){
                    return find.where().eq("transaction_status", status).findList();
                 }
    
    public void shipped(){
        date_shipped = new Date();        
        transaction_status = "Shipped";
        save();
    }

}

		 
