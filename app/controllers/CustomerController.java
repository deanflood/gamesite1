package controllers;
import models.*;
import play.mvc.Security;
import play.data.*;
import play.mvc.*;
import play.mvc.Result;
import play.mvc.Controller;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import com.avaje.ebean.*;
import views.html.*;
import views.html.backend.*;
import views.html.frontend.*;
import play.mvc.Http.Request;
import static play.data.Form.*;

/**
 *
 * @author Dean
 */
public class CustomerController extends Controller {
    
    private static final Form<Customer> customerForm = Form.form(Customer.class);
    private static final Form<Login> loginForm = Form.form(Login.class);
    private static final Form<Contact> contactForm = Form.form(Contact.class);
    
    
    //Page Renders      
    
     public static Result register() {        
       return ok(register.render(customerForm, Customer.findByEmail(currentSeshEmail())));
    }
     
      public static Result pay(Long cust_id, Long cart_id) {
          Customer customer = Customer.findByCust_id(cust_id);
          ShoppingCart cart = ShoppingCart.returnCartObject(cust_id);
          cart.changeStatus("Pending");
          Product.reduceStockFromOrderDetails(OrderDetails.getOrderDetailList(cart_id));
          ShoppingCart sc = new ShoppingCart();
          sc.setCustId(cust_id);
          sc.setDefaultBillingAddress(customer.address1, customer.address2, customer.city, customer.country);
          cart.date_made= new Date();
          cart.save();
          sc.save();
          flash("success", "Payment Received. Thank you for your custom!");            
       return ok(account.render(Customer.findByEmail(currentSeshEmail()), ShoppingCart.returnHistory((Customer.findByEmail(currentSeshEmail()).cust_id))));
    } 
    
     @Security.Authenticated(SecuredPortal.class)
    public static Result customerList(){
        List<Customer> custs = Customer.findAll();
        return ok(customerList.render(custs));
    }
    
     @Security.Authenticated(Secured.class)
     public static Result account(){
         Customer c = Customer.findByEmail(currentSeshEmail());
         return ok(account.render(c, ShoppingCart.returnHistory(c.cust_id)));
     }
         
    
    @Security.Authenticated(Secured.class)
	public static Result cart(){  
            DynamicForm bindedForm = form().bindFromRequest();
            String cartId = bindedForm.get("cartId");           
            String qty = bindedForm.get("qty");           
            String pid = bindedForm.get("pid"); 
            System.out.println(qty);
            Customer c = Customer.findByEmail(currentSeshEmail());            
            try{                
                if(Integer.parseInt(qty) == 0){
                   OrderDetails.removeFromCart(Long.parseLong(cartId), Long.parseLong(pid));
                   return ok(cart.render(c, OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(c.cust_id)), Product.getProdFromOrderDetails(OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(c.cust_id)))));

                }
                if(Integer.parseInt(qty) > Product.findByprod_id(Long.parseLong(pid)).stock_level){
                    flash("success", "Not enough stock, please try another amount");    
                return ok(cart.render(c, OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(c.cust_id)), Product.getProdFromOrderDetails(OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(c.cust_id)))));
                }
                if(qty != null){
                OrderDetails.changeQuantity(Long.parseLong(cartId), Long.parseLong(pid), Integer.parseInt(qty));
                }
            
            }catch(NumberFormatException e){
                if(qty != null){
                    flash("success", "Please enter a valid number"); 
                }
               return ok(cart.render(c, OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(c.cust_id)), Product.getProdFromOrderDetails(OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(c.cust_id)))));
            }            
            return ok(cart.render(c, OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(c.cust_id)), Product.getProdFromOrderDetails(OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(c.cust_id)))));
        }
        
        public static Result purchase() {    
            Customer c = Customer.findByEmail(currentSeshEmail());
       return ok(purchase.render(c, ShoppingCart.returnCartObject(c.cust_id)));
    }
        
     
            
        
     @Security.Authenticated(SecuredPortal.class)
        public static Result editCustomer(Long cid){
        final Customer customer = Customer.findByCust_id(cid);
        if(customer == null){
            return notFound(String.format("Customer does not exist"));
        }
        Form<Customer> filledform = customerForm.fill(customer);
        return ok(editCustomer.render(filledform));
    }
        @Security.Authenticated(Secured.class)
        public static Result editAccount(Long cid){
        final Customer customer = Customer.findByCust_id(cid);
        Form<Customer> filledform = customerForm.fill(customer);
        return ok(editMyAccount.render(filledform, customer));
    }
    
    @Security.Authenticated(SecuredPortal.class)
   public static Result deleteCustomer(Long cid) {
     final Customer customer = Customer.findByCust_id(cid);
     
    if(customer == null) {
        return notFound(String.format("Customer %s does not exists.", cid));
    }
    customer.delete();
    return redirect(routes.CustomerController.customerList());
  }

         //POST Methods   
    public static Result custSubmit(){
        Form<Customer> boundForm = customerForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
        return badRequest(register.render(boundForm, Customer.findByEmail(currentSeshEmail())));
    }
     Customer c = boundForm.get();
     if(c.cust_id==null){
        if(Customer.checkUniqueEmail(c.email)){
            c.sha1Password();
            c.save();
            ShoppingCart sc = new ShoppingCart();
            sc.setCustId(c.cust_id);
            sc.setDefaultBillingAddress(c.address1, c.address2, c.city, c.country);
            sc.save();
        }else{
            flash("success", "Email is already used, please use another");     
            return badRequest(register.render(boundForm, Customer.findByEmail(currentSeshEmail()))); 
        }
        
    }
    else{
        c.sha1Password();
        c.update();
     }   
     flash("success", "You have been successfully registered, please login"); 
    return redirect(routes.CustomerController.login());
    }
    
    public static Result custEdit(){
        Form<Customer> boundForm = customerForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
        return badRequest(register.render(boundForm, Customer.findByEmail(currentSeshEmail())));
    }
    Customer c = boundForm.get();
        c.sha1Password();
        c.update();
        
    return redirect(routes.CustomerController.account());
    }
    
    public static Result adminCustEdit(){
        Form<Customer> boundForm = customerForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
        return badRequest(register.render(boundForm, Customer.findByEmail(currentSeshEmail())));
    }
    Customer c = boundForm.get();
        c.sha1Password();
        c.update();
        
        flash("success", "Account Updated");
    return redirect(routes.ManagementController.portal());
    }
    
    
  
    
    
    public static Result contactSubmit(){
        Form<Contact> boundForm = contactForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
        return badRequest(contact.render(boundForm, Customer.findByEmail(currentSeshEmail())));
    }
     Contact c = boundForm.get();
     if(c.contact_id==null){        
        c.save();  
    }
    else{       
        c.update();
        return redirect(routes.ManagementController.contactList());
     }
    flash("success", "Your contact has been submitted. Please allow up to 24 hours for us to contact you.");
    return redirect(routes.Application.contact());
  }
    
    
    
    //Login Methods
  public static Result login(){        
        return ok(login.render(loginForm, Customer.findByEmail(currentSeshEmail())));
    }
    
    public static Result authenticate() {
    Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
    if (loginForm.hasErrors()) {
        return badRequest(login.render(loginForm, Customer.findByEmail(currentSeshEmail())));
    } else {
        Secured s= new Secured();
        session().clear();
        session("email", loginForm.get().email);
        
     
        
        return redirect(
            routes.Application.index()
        );
    }
}
    
    public static Result logout() {
    session().clear();
    flash("success", "You've been logged out");
    return redirect(
        routes.CustomerController.login()
    );
}
    
    public static String currentSeshEmail(){
        Secured s  = new Secured();
        return s.getUsername(Http.Context.current());
    }
    
    public static Result wishList(){
        Customer c = Customer.findByEmail(currentSeshEmail());
        return ok(wishlist.render(c, Product.getProdForWishlist(WishListItem.getWishList(c.cust_id))));
                }
    
    public static Result addToWishList(Long custId, Long prodId){        
       WishListItem w = new WishListItem();
       if(WishListItem.itemExists(custId, prodId)){
           return redirect(routes.CustomerController.wishList());
           }
       else{       
       w.setCustId(custId);       
       w.setProductId(prodId);
       w.save();       
       return redirect(routes.CustomerController.wishList());
       }        
    }
    
    public static Result removeFromWishlist(Long custId, Long pid){
        WishListItem.removeFromWishList(custId, pid);
        return redirect (routes.CustomerController.wishList());
    }
    


        

    
    public static class Login {
    public String email;
    public String password;    
    public String validate() {
    if (Customer.authenticate(email, password) == null) {
        flash("success", "Incorrect email or password entered.");
      return "Invalid user or password";
    }
    return null;
}

  
}
    
}
