package controllers;
import models.*;
import play.mvc.Security;
import play.data.*;
import play.mvc.*;
import play.mvc.Result;
import play.mvc.Controller;
import java.util.ArrayList;
import java.util.List;
import com.avaje.ebean.*;
import views.html.*;
import views.html.backend.*;
import views.html.frontend.*;
import static play.data.Form.*;

//Imports for File upload
import java.io.File;
import static play.mvc.Http.MultipartFormData;
import java.io.IOException;
import com.google.common.io.Files;




public class ManagementController extends Controller { 
    
    private static final Form<Employee> employeeForm = Form.form(Employee.class);
    private static final Form<Login> loginForm = Form.form(Login.class);    
    private static final Form<Contact> contactForm = Form.form(Contact.class);
    private static final Form<Product> productForm = Form.form(Product.class);
   
     @Security.Authenticated(SecuredPortal.class)
    public static Result portal() {        
    return ok(portal.render()); 
    }
    
            public static Result empSubmit(){
        Form<Employee> boundForm = employeeForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("error", "Please correct the form below.");
        return badRequest(addAdmin.render(boundForm));
    }
     Employee e = boundForm.get();
     if(e.emp_id==null){
        if(Employee.checkUniqueUsername(e.username)){
            e.sha1Password();
            e.save();
            
        }else{
            flash("success", "Username is already used, please use another");     
            return badRequest(addAdmin.render(boundForm)); 
        }
        
    }
    else{
        e.sha1Password();
        e.update();
     }   
    return redirect(routes.ManagementController.portal());
    }
    
             @Security.Authenticated(SecuredPortal.class)
    public static Result addAdmin(){
        return ok(addAdmin.render(employeeForm));
    }
      public static Result employeelogin(){
        return ok(employeeLogin.render(loginForm));
    }
    
    public static Result employeeauthenticate() {
    Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
    if (loginForm.hasErrors()) {
        return badRequest(employeeLogin.render(loginForm));
    } else {
        session().clear();
        session("username", loginForm.get().username);
        return redirect(
            routes.ManagementController.portal()
        );
    }
}
     public static Result confirmProdEdit() {
         System.out.println("Confirm prod edit ");
    Form<Product> boundForm = productForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("success", "Please correct the form below.");
      return badRequest(editProduct.render(boundForm));
    }
    Product product = boundForm.get();
     if(product.stock_level < 0){
        flash("success", "Enter valid stock level");
        System.out.println("#############");
      return badRequest(editProduct.render(boundForm));
    }
    MultipartFormData body = request().body().asMultipartFormData();
    MultipartFormData.FilePart part = body.getFile("image");
    
    if(part != null) {
      File image = part.getFile();
      try {
        product.image = Files.toByteArray(image);
        
      }
      catch (IOException e) {
        return internalServerError("Error reading file upload");
      }
    }    
    if(product.prod_id==null){
        product.save();  
    }
    else{
        product.update();
     }        
    flash ("success", "Product Updated");
    return redirect(routes.ManagementController.portal());
    }
    
    @Security.Authenticated(SecuredPortal.class)
    public static Result contactList(){
        List<Contact> contacts = Contact.findAll();
        return ok(contactList.render(contacts));
    }
    
     public static  Result contactByStatus(String status){
        List<Contact> contacts = Contact.byStatus(status);
        return ok(contactList.render(contacts));        
    }
     
     public static  Result orderByStatus(String status){
        List<ShoppingCart> cart = ShoppingCart.byStatus(status);
        return ok(orders.render(cart));        
    }
     
     public static Result orderDetails(Long cartId){
       ShoppingCart cart = ShoppingCart.returnByCartId(cartId);
       Customer customer = Customer.findByCust_id(cart.cust_id);
       List<OrderDetails> odList = OrderDetails.getOrderDetailList(cartId);
       List<Product> prodList = Product.getProdFromOrderDetails(odList);
       for(int i = 0;i<OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(cart.cust_id)).size(); i++){
           System.out.println(OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(cart.cust_id)).get(i).prod_id);
       }
       return ok(orderDetails.render(customer, cart, odList, prodList));
     }
    
    @Security.Authenticated(SecuredPortal.class)
        public static Result updateContact(Long cid){
        final Contact contact = Contact.findByContact_id(cid);
        if(contact == null){
            return notFound(String.format("Contact does not exist does not exist"));
        }
        Form<Contact> filledform = contactForm.fill(contact);
        return ok(updateContact.render(filledform));
    }
        
    @Security.Authenticated(SecuredPortal.class)
        public static Result orders(){
            return ok(orders.render(ShoppingCart.findAll()));
        }
    
    public static Result logout() {
    session().clear();
    flash("success", "You have been logged out");
    return ok(employeeLogin.render(loginForm));
    }
    public static Result createsale(){
        try{DynamicForm bindedForm = form().bindFromRequest();
            String genre = bindedForm.get("genre");             
            String percentage = bindedForm.get("percentage"); 
            if(percentage.isEmpty()){
                
                 flash("success", "Please enter a percentage:");
                return ok(createSale.render());
            }
            
        
            double percentage1 = Double.parseDouble(percentage); 
            if(percentage1>99 || percentage1 <1){
                flash("success", "Please enter a percentage between 1 and 99");
                return ok(createSale.render());
            }
            List<Product>prod = Product.searchByGenre(genre);
            if(prod.size()==0){
                flash("success","No games in this category");
                return ok(createSale.render());
            }
            else{
            for(int i = 0; i < prod.size(); i++){                
                Product p = prod.get(i);
                 if(p.sale_price != 0){
                 flash("success", "Sale Already in place for:" +genre);
                return ok(createSale.render());
                 }
                double price = prod.get(i).getPrice();
                p.setSalePrice(price);        
                double newSalePrice= prod.get(i).getPrice() - (prod.get(i).getPrice() * (percentage1/100));        
                p.setPrice(newSalePrice);
                p.save();
            }
            }
            }catch(NumberFormatException e){
            flash("success", "Please enter a valid number:");
                return ok(createSale.render());
            }  
        flash("success","New Sale Activated");
        return ok(portal.render());
    }
    
    public static Result undoSale(){
        try{
        DynamicForm bindedForm = form().bindFromRequest();
            String genre = bindedForm.get("undogenre");  
            List<Product>prod = Product.searchByGenre(genre);
             if(prod.get(0).sale_price == 0){
                 flash("success", "Sale is not activated");
            return ok(createSale.render());
             }
             else{
            for(int i = 0; i < prod.size(); i++){
            Product p = prod.get(i);     
           
            p.setPrice(p.getSalePrice());
            p.setSalePrice(0);
            p.save();
            
            }
            
             flash("success", "Sale Removed");
            return ok(portal.render());
    }
        }
        catch(IndexOutOfBoundsException x){
            flash("success", "No Games in this Category");
                return ok(createSale.render());
        }
    }
    
     @Security.Authenticated(SecuredPortal.class)
    public static Result adminProdList(){
     return ok(adminProdPage.render(Product.findAll()));   
    }
    
    public static Result cancelOrder(Long shid){
        List<OrderDetails>odList = OrderDetails.getOrderDetailList(shid);
        for(int i = 0; i < odList.size() ; i++){           
           OrderDetails.cancelOrderDetails(odList.get(i));
            Product.increaseStock(odList.get(i).prod_id, odList.get(i).qty);
        }
        ShoppingCart.cancelCart(shid);
         flash("success", "Order Cancelled");
         return ok(orders.render(ShoppingCart.findAll()));
    }
    
        
        
         @Security.Authenticated(SecuredPortal.class)
        public static Result sale(){
            return ok(createSale.render());
        }
        
         @Security.Authenticated(SecuredPortal.class)
          public static Result reports() {
            return ok(miniportal.render());
    }
        
           @Security.Authenticated(SecuredPortal.class)
        public static Result customerreports() {
            return ok(customerReport.render());
    }
         @Security.Authenticated(SecuredPortal.class)
        public static Result genrereports() {
            return ok(genreReport.render());
    }
         @Security.Authenticated(SecuredPortal.class)
        public static Result profitreports() {
            return ok(profitReport.render(Product.findAll()));
    }
        

    
    public static class Login {
    public String username;
    public String password;    
    public String validate() {
    if (Employee.authenticate(username, password) == null) {
        flash("success", "Incorrect Username or Password entered.");
      return "Invalid user or password";
    }
    return null;
} 
}
    
}

       




