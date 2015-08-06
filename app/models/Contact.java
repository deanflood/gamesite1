package models;
import java.text.SimpleDateFormat;
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
public class Contact extends Model{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                
                
		@Id
		public Long contact_id;	
                @Constraints.Email
                @Constraints.Required
		public String cust_email;
		public String topic;
		public String query;
		public Date date_created = new Date();
		public String status = "Open";
		
		public Contact(){}
		
		
		public static Finder<Long, Contact> find = new Finder<Long, Contact>(Long.class, Contact.class);
                
                public static List<Contact> findAll() {
                return Contact.find.all();
  }
                
                public static Contact findByContact_id(Long contact_id){
                    return find.where().eq("contact_id", contact_id).findUnique();
                 }
                
                public static List<Contact> byStatus(String status){
                    return find.where().eq("status", status).findList();
                 }
		}
		
		
		