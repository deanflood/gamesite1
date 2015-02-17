package controllers;
import models.Customer;
import models.Login;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Controller;
import java.util.ArrayList;
import java.util.List;
import com.avaje.ebean.*;
import views.html.*;

public class Application extends Controller {
    
   
    
    
    private static final Form<Customer> customerForm = Form.form(Customer.class);
    private static final Form<Login> loginForm = Form.form(Login.class);

    public static Result index() {
        return ok(index.render());
    }
    
    public static Result register() {
       return ok(register.render(customerForm));
    }
    
    public static Result custList(){
        List<Customer> custs = Customer.findAll();
        return ok(custList.render(custs));
    }
	
    public static Result login(){
        //List<Login> login = Login.findAll();
        return ok(login.render(loginForm));
    }
	
	public static Result cart(){
       return ok(cart.render());
    }
	
	public static Result contact(){
       return ok(contact.render());
    }
    
        
    public static Result custSubmit(){
        Form<Customer> boundForm = customerForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
      return badRequest(register.render(boundForm));
    }
     Customer c = boundForm.get();
    c.sha1Password();
    c.save();
    return redirect(routes.Application.custList());
  }
    
    public static Result attemptLogin(){
        Form<Login> LoginBoundForm = loginForm.bindFromRequest();
    if(LoginBoundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
      return badRequest(login.render(LoginBoundForm));
    }
     Login l = LoginBoundForm.get();
    l.sha1Password();
    l.save();
    return redirect(routes.Application.index());
  }

}
