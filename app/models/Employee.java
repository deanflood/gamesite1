package models;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.*;
import play.data.validation.Constraints;
//DB Imports
import javax.persistence.*;
import play.db.ebean.*;
//Imports required for Message Digest
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
public class Employee extends Model{
@Id
public Long emp_id;
//@Constraints.Required
public String emp_fname;
public String emp_lname;
public String username;
public String password;
public String emp_type;

public Employee(){
}
public Employee(String fname, String lname, String uname, String pass, String type){
emp_fname=fname;
emp_lname = lname;
username= uname;
password= pass;
emp_type = type;
}

public static Finder<Long, Employee> find = new Finder<Long, Employee>(Long.class, Employee.class);

public static Employee authenticate(String username, String password) {
        try{
            password= sha1(password);            
       }catch(NoSuchAlgorithmException e){
          e.getMessage();
   }
        return find.where().eq("username", username).eq("password", password).findUnique();
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

public static boolean checkUniqueUsername(String username){
    boolean check;
    if(find.where().eq("username", username).findUnique() == null){
        check = true;
                }
    else{
        check = false;
    }
    return check;
}
	}
	