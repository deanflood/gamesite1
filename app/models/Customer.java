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
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        
        @Id
        public Long id;
         //@Constraints.Required
	public String cust_id;
         //@Constraints.Required
	public String cust_fname;
         //@Constraints.Required
	public String cust_lname;
        // @Constraints.Required
	public Date dateOfBirth; 
        // @Constraints.Required
        public String email;
        // @Constraints.Required
	public String address1;
         //@Constraints.Required
	public String address2;
	public String city;
        public String phoneNum;
         //@Constraints.Required
        public String password; 
        
public Customer(){
}

public Customer(String fn, String ln, Date dob, String em, String a1, String a2, String c, String pn, String pw){
        
    cust_fname = fn;
    cust_lname = ln;
    dateOfBirth = dob;        
    email = em;
    address1 = a1;
    address2 = a2;
    city = c;
    phoneNum = pn;
    password = pw; 
    id++;
}
    
public static Finder<Long, Customer> find = new Finder<Long, Customer>(Long.class, Customer.class);


public static List<Customer> findAll() {
    return Customer.find.all();
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
        


}
