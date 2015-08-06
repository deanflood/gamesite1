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
import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;
import com.avaje.ebean.*;
import com.avaje.ebean.Query;


@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class OrderDetails extends Model{
@Id
public Long shoppingcart_id;
@Id
public Long prod_id;
public int qty;
public double line_total;

public OrderDetails(){}


public static Finder<Long, OrderDetails> find = new Finder<Long, OrderDetails>(Long.class, OrderDetails.class);

 public static List<OrderDetails> findAll() {
                    return OrderDetails.find.all();
                 }
 

public void setCartId(Long cartId){
    shoppingcart_id = cartId;
}

public void setProductId(Long pid){
    prod_id = pid;
}

public void setQty(int q){
    qty = q;
}

public int getQty(){
    return qty;
}

public void setLineTotal(int q, double p){
    line_total = q * p;
}

public Long getProductId(){
    return prod_id;
}

public String toString(){
    return " "+prod_id;
}



public static List<OrderDetails> getOrderFromCart(Long cartId){
     return find.where().eq("shoppingcart_id", cartId).findList();
}
    

public static List<OrderDetails> getOrderDetailList(Long cartId){   
                     return find.where().eq("shoppingcart_id",cartId).findList();
                 }

public static boolean itemExists(Long cartId, Long prodId){
    boolean check;
    if((find.where().eq("shoppingcart_id", cartId).eq("prod_id", prodId).findUnique()) == null){
        check = false;
    }else{
        check = true;
    }
    return check;
}

public static void removeFromCart(Long cartId, Long pid){
    OrderDetails o = find.where().eq("shoppingcart_id", cartId).eq("prod_id", pid).findUnique();
    o.delete();
}

public static void changeQuantity(Long cartId, Long prodId, int qty){
    OrderDetails o = find.where().eq("shoppingcart_id", cartId).eq("prod_id", prodId).findUnique();
    Product p = Product.findByprod_id(prodId);
    o.setQty(qty);
    o.setLineTotal(qty, p.price);
    o.save();
}

 public static double getProductSubTotal(Long id){
        double total=0;
        List<OrderDetails> od = OrderDetails.find.where().eq("prod_id",id).findList();      
        for (int i = 0; i < od.size(); i++){
            total=total+od.get(i).getLineTotal();
    }
        System.out.println("SUBTOTAL "+total);
        return total;
        
    }
 public double getLineTotal(){
    return line_total;
}
 
  public static int genreCount(String genre){
                    List<Product> prods = Product.find.where().eq("genre",genre).findList();                
                    ArrayList<OrderDetails>o1= new ArrayList<OrderDetails>();
                    
                    
                    
                    for(int i =0; i <prods.size(); i++){
                        Long id = (prods.get(i).getId());
                     
                       List<OrderDetails>  orders =find.where().eq("prod_id", id).findList();
                         
                       o1 = new ArrayList<OrderDetails>(orders);                         
                    }
                   return o1.size();
                 }
  


  
  
  public static void cancelOrderDetails(OrderDetails o){
            Product.increaseStock(o.prod_id, o.qty);
            o.setQty(0);
            o.line_total = 0; 
            o.save();
  }
     public static double getTopSellerAmount(int pos){
      double line_total=0;
 RawSql rawSql = RawSqlBuilder.parse("select prod_id, sum(line_total) from order_details group by prod_id order by sum(line_total) desc")
                  .columnMapping("sum(line_total)", "line_total")
                  .columnMapping("prod_id", "prod_id")
              
                  .create();

Query<OrderDetails> query = Ebean.find(OrderDetails.class);
query.setRawSql(rawSql);
List<OrderDetails> result = query.findList();
Long id =result.get(pos).getProductId();
Product p = Product.findByprod_id(id);
line_total= result.get(pos).getLineTotal();
return line_total;

  }
  
  public static String getTopSellerName(int pos){
      
      RawSql rawSql = RawSqlBuilder.parse("select prod_id, sum(line_total) from order_details group by prod_id order by sum(line_total) desc")
                  .columnMapping("sum(line_total)", "line_total")
                  .columnMapping("prod_id", "prod_id")
              
                  .create();

Query<OrderDetails> query = Ebean.find(OrderDetails.class);
query.setRawSql(rawSql);
List<OrderDetails> result = query.findList();
Long id = result.get(pos).getProductId();
System.out.println("ID:  "+id);
Product p = Product.findByprod_id(id);
        

return "";

  }
  
  public static void deleteOrderDetails(Long pid){
      SqlUpdate down = Ebean.createSqlUpdate("DELETE FROM order_details WHERE prod_id = '" +pid+"'");
      down.execute(); 
  }

}



