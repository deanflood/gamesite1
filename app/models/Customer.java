package models;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.*;
//Imports required for Message Digest
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import play.data.validation.Constraints;
//DB Imports
import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Customer extends Model  {        
        
@Id
    public Long cust_id;
@Constraints.Required
    public String cust_fname;
@Constraints.Required
    public String cust_lname;
@Constraints.Required
    public Date date_of_birth; 
@Constraints.Email
@Constraints.Required
    public String email;
@Constraints.Required        
    public String address1;         
    public String address2;
    public String city;
@Constraints.Required
    public String country;
    public String phoneNum;
@Constraints.Required
    public String password; 
        

        
public Customer(){
}


public static Finder<Long, Customer> find = new Finder<Long, Customer>(Long.class, Customer.class);


public static List<Customer> findAll() {
    return Customer.find.all();
  }

public static Customer findByEmail(String email){
      return find.where().eq("email", email).findUnique();
                 }
public static Customer findByCust_id(Long cust_id){
                    return find.where().eq("cust_id", cust_id).findUnique();
                 }


public  void sha1Password(){
   try{ password = sha1(password);
    }
 catch(NoSuchAlgorithmException e){
        e.getMessage();
    }
}
   

static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

public static Customer authenticate(String email, String password) {
        try{
            password= sha1(password);            
        }catch(NoSuchAlgorithmException e){
            e.getMessage();
    }
        return find.where().eq("email", email).eq("password", password).findUnique();
}

public static Long getCustId(String email){
    Customer c=find.where().eq("email", email).findUnique();
    return c.cust_id;
}

public static boolean checkUniqueEmail(String email){
    boolean check;
    if(find.where().eq("email", email).findUnique() == null){
        check = true;
                }
    else{
        check = false;
    }
    return check;
}
    
public static int searchByCountry(String country){
    List<Customer>custs;
    if(country.equals("Ireland")||country.equals("UK")||country.equals("USA")){
    
                    custs =  find.where().eq("country",country).findList();
    }


    else{
                   custs = find.where().ne("country","Ireland").ne("country","USA").ne("country","UK").findList();
        } 
                   return custs.size();
                 }


}
