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
public class Login extends Model  {
    @Id
    public Long id;
    public String emailIn;
    public String passwordIn;
    
    public Login(){}
    
    public Login(String e, String p){
        emailIn = e;
        passwordIn = p;
    }
    
    public static Finder<Long, Login> find = new Finder<Long, Login>(Long.class, Login.class);
    
    public static List<Login> findAll() {
    return Login.find.all();
  }
    
    public  void sha1Password(){
   try{ passwordIn = sha1(passwordIn);
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
