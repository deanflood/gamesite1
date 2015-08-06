package models;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.*;
import play.data.validation.Constraints;
//DB Imports
import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;

 @Entity
public class WishListItem extends Model{
    
    @Id
    public Long cust_id;
    @Id
    public Long prod_id;
    
    public static Finder<Long, WishListItem> find = new Finder<Long, WishListItem>(Long.class, WishListItem.class);


public WishListItem(){}

public static boolean itemExists(Long custId, Long prodId){
    boolean check;
    if((find.where().eq("cust_id", custId).eq("prod_id", prodId).findUnique()) == null){
        check = false;
    }else{
        check = true;
    }
    return check;
}

public void setCustId(Long custId){
    cust_id = custId;
}

public void setProductId(Long pid){
    prod_id = pid;
}

public Long getProductId(){
    return prod_id;
}

public static void removeFromWishList(Long custId, Long pid){
    WishListItem w = find.where().eq("cust_id", custId).eq("prod_id", pid).findUnique();
    w.delete();
}

public static List<WishListItem> getWishList(Long custId){
    return find.where().eq("cust_id", custId).findList();
}


}
            
