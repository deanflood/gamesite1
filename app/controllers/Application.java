package controllers;
import models.Customer;
import models.Product;
import play.mvc.Security;
import play.data.*;
import play.mvc.*;
import models.*;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Controller;
import java.util.ArrayList;
import java.util.List;
import com.avaje.ebean.*;
import views.html.*;
import views.html.backend.*;
import views.html.frontend.*;
import static play.data.Form.*;


import play.mvc.Http.Context;

public class Application extends Controller { 
    
    public static final Form<Contact> contactForm = Form.form(Contact.class);
   
    public static Result index() {        
        Secured s  = new Secured();
        String email = s.getUsername(Http.Context.current());         
    return ok(index.render(Customer.findByEmail(email), Product.findTopRating())); 
        
    }
    
    public static Result shopfront() {
        return ok(shopfront.render());
    }
    
    
        
   
    
    public static Result advanceSearch() {
        return TODO;
    }
    
    public static Result contact(){
        Secured s  = new Secured();
        String email = s.getUsername(Http.Context.current()); 
       return ok(contact.render(contactForm, Customer.findByEmail(email)));
    }
    
    
    
}



