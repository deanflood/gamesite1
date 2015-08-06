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
import play.mvc.Http.Request;
import static play.data.Form.*;
import java.util.Collections;


//Imports for File upload
import java.io.File;
import static play.mvc.Http.MultipartFormData;
import java.io.IOException;
import com.google.common.io.Files;

public class ProductsController extends Controller { 
   
    //Form Declarations     
    private static final Form<Product> productForm = Form.form(Product.class);
 

    //Page Renders
    //@Security.Authenticated(SecuredPortal.class)
    public static Result productOverview(){
        List<Product> prod = Product.findAll();
        return ok(productOverview.render(prod, Customer.findByEmail(currentSeshEmail())));
    }
       
        public static Result genreView(String genre){
        List<Product>prod=Product.searchByGenre(genre);
        return ok(productOverview.render(prod, Customer.findByEmail(currentSeshEmail())));
    }
        
        public static Result platformView(String platform){
        List<Product>prod=Product.searchByPlatform(platform);
        return ok(productOverview.render(prod, Customer.findByEmail(currentSeshEmail())));
    }
         public static Result companyView(String platform,String platform2){
        List<Product>prod=Product.searchByCompany(platform,platform2);
        return ok(productOverview.render(prod, Customer.findByEmail(currentSeshEmail())));
    }
        
        public static Result priceView(double price){
            List<Product>prod=Product.searchByPrice(price);
            return ok(productOverview.render(prod,Customer.findByEmail(currentSeshEmail())));
        }
        
        public static Result saleView(){
            List<Product>prod=Product.saleProducts();
            return ok(productOverview.render(prod,Customer.findByEmail(currentSeshEmail())));
        }
       
        
        public static Result nameView(){
            DynamicForm bindedForm = form().bindFromRequest();
            String search = bindedForm.get("search");   
            List<Product>prod=Product.searchByName(search);
            return ok(productOverview.render(prod,Customer.findByEmail(currentSeshEmail())));
        }
        
        
        public static Result reviewGame(Long pid, int i){
            System.out.println(i);
            Product p = Product.findByprod_id(pid);
            p.incrementNumberOfReviews();
            p.increaseReviewScore(i);
            p.save();
            System.out.println(p.review_score/p.number_of_reviews);
            return redirect(routes.ProductsController.displayProduct(pid));
        }
            
	
    @Security.Authenticated(SecuredPortal.class)   
    public static Result addProduct(){        
        return ok(addProduct.render(productForm));
    }
    
    public static Result displayProduct(Long prod_id){
        Product product = Product.findByprod_id(prod_id);
        return ok(productPage.render(product, Customer.findByEmail(currentSeshEmail())));
    }
    
     @Security.Authenticated(SecuredPortal.class)
    public static Result markAsShipped(Long cartId){
        ShoppingCart cart = ShoppingCart.returnByCartId(cartId);
        cart.shipped();
        flash("success", "Order Marked as Shipped");
        return redirect(routes.ManagementController.orders());
         }    
 
    
    public static Result checkOut(){ 
        DynamicForm bindedForm = form().bindFromRequest();
            String address1 = bindedForm.get("address1");           
            String address2 = bindedForm.get("address2");           
            String city = bindedForm.get("city");
            String country = bindedForm.get("country"); 
          System.out.println(address1);
          try{Customer cust = Customer.findByEmail(currentSeshEmail());            
          ShoppingCart cart = ShoppingCart.returnCartObject(cust.cust_id); 
          cart.calcShippingCost();
          cart.calcOrderTotal(cart.shoppingcart_id);
          cart.calcSubtotal();
          if(address1 != null){
              cart.setDefaultBillingAddress(address1, address2, city, country);
              cart.calcShippingCost();
              cart.calcSubtotal();
          }
          cart.save();
          return ok(checkOut.render(cust, cart, OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(cust.cust_id)), Product.getProdFromOrderDetails(OrderDetails.getOrderDetailList(ShoppingCart.returnOpenCart(cust.cust_id)))));

          }catch(NullPointerException e){
              return redirect (routes.CustomerController.cart());
          }

    }
   
  
   
    
    public static Result save() {
    Form<Product> boundForm = productForm.bindFromRequest();
    if(boundForm.hasErrors()) {
      flash("success", "Please correct the form below.");
      return badRequest(addProduct.render(boundForm));
    }

    Product product = boundForm.get();
        if(product.stock_level < 0){
        flash("success", "Stock must be set at 0 or more");
      return badRequest(addProduct.render(boundForm));
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
    flash ("success", "Product Added");
    return redirect(routes.ManagementController.portal());
    }
    
    
    
    public static Result edit(Long pid){
        final Product product = Product.findByprod_id(pid);
        if(product == null){
            return notFound(String.format("Product does not exist"));
        }
        Form<Product> filledform = productForm.fill(product);
        return ok(editProduct.render(filledform));
    }
    
   public static Result delete(Long pid) {
    final Product product = Product.findByprod_id(pid);
    if(product == null) {
        return notFound(String.format("Product %s does not exists.", pid));
    }
    OrderDetails.deleteOrderDetails(product.prod_id);
    product.delete();
    return redirect(routes.ManagementController.adminProdList());
  }
   
   public static Result addToCart(Long cid, Long pid){
       OrderDetails o = new OrderDetails();
       if(OrderDetails.itemExists(ShoppingCart.returnOpenCart(cid), pid)){
           return redirect(routes.CustomerController.cart());
           }
       else{       
       o.setCartId(ShoppingCart.returnOpenCart(cid));       
       o.setProductId(pid);
       o.setQty(1);
       o.setLineTotal(1, Product.priceById(pid));
       o.save();       
       return redirect(routes.CustomerController.cart());
       }
       
       //return ok(cart.render(Customer.findByEmail(currentSeshEmail())));
   }
   
   

    public static Result image(Long prod_id){
        final Product product = Product.findByprod_id(prod_id);
        if(product==null) return ok();
                return ok(product.image);
    }

    public static Result removeFromCart(Long cartId, Long pid){
        OrderDetails.removeFromCart(cartId, pid);
        return redirect (routes.CustomerController.cart());
    }
        
        
    public static String currentSeshEmail(){
        Secured s  = new Secured();
        return s.getUsername(Http.Context.current());
    }
    
    public static Result changeQuantity(){
        System.out.println("###################");
        return redirect (routes.CustomerController.cart());
    }
    
    public static Result viewbyRating(){
        List<Product> pList = Product.findTopRating();
        
        for(int i = 0;i<pList.size();i++){
            System.out.println(pList.get(i).getRating());
        }
            //System.out.println(Comparator.compare(pList.get(0), pList.get(1)));
        
         return ok(productOverview.render(pList, Customer.findByEmail(currentSeshEmail())));
    }
        
    
   
    
}

    
    



