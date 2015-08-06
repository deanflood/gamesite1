package models;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.*;
import play.data.validation.Constraints;
//DB Imports
import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import play.libs.F.Option;
import java.util.Collections;
import java.util.Comparator;

import javax.persistence.Entity;
//import play.db.jpa.Model;
import java.*;

@Entity
public class Product extends Model{
@Id
public Long prod_id;
         //@Constraints.Required
		 public String prod_name;
		 public String prod_desc;
		 public String genre;
		 public int age_rating;
		 public double review_score = 0;
		 public double price;
		 public double sale_price;
		 public String platform;
		 public int stock_level;
		 public String prod_type = "game";
		 public byte[] image;
		 public String supplier;
                 public Integer number_of_reviews = 0 ;
		 
		 public Product(){
		 }
		 

		 public static Finder<Long, Product> find = new Finder<Long, Product>(Long.class, Product.class);
		 
                 public static List<Product> findAll() {
                     return find.where().orderBy("prod_name").findList();
//return Product.find.all().orderBy("prod_name");
                 }
                 
                 public static List<Product>searchByGenre(String genre){
                 return find.where().ilike("genre","%"+genre+"%").findList();
                }
                 
                 public static List<Product>searchByCompany(String platform,String platform2){
                 return find.where().or(Expr.ilike("platform","%"+platform+"%"),Expr.ilike("platform","%"+platform2+"%" )).findList();
                }
                 
                 public static List<Product>searchByCategory(String category){
                     return find.where().ilike("category","%"+category+"%").findList();
                 }
                 
                 public static List<Product>searchByPlatform(String platform){
                     return find.where().ilike("platform","%"+platform+"%").findList();
                 }
                 
                 public static List<Product>searchByName(String name){
                 return find.where().ilike("prod_name","%"+name+"%").findList();
                }
                 
                // public static List<Product>advanceSearch(Option<String> genre, Option<String> platform, Option<String> age_rating, Option<String> price){
                public static List<Product>advanceSearch(String genre, String platform, String age_rating, String price){
    
                     ExpressionList<Product>productQuery= find.where();
                     if(genre != null)productQuery.add(Expr.eq("genre",genre));
                     if(genre==null)productQuery.add(Expr.eq("genre"," "));
                     if(platform != null)productQuery.add(Expr.eq("platform", platform));
                     if(platform==null)productQuery.add(Expr.eq("platform"," "));
                     //if(age_rating != null)productQuery.add(Expr.eq("age_rating", age_rating));
                     if(age_rating==null)productQuery.add(Expr.eq("age_rating"," "));
                     if(price!= null)productQuery.add(Expr.eq("price", price));
                     if(price == null)productQuery.add(Expr.le("price",100));
                     return productQuery.findList();
                     //return find.where().or(Expr.eq("genre","%"+genre+"%"),Expr.eq("platform","%"+platform+"%")).findList();
                 }
                 
                 public static List<Product>searchByPrice(double price){
                     return find.where().le("price",price).findList();
                 }              
                 
                 
                 public static Product findByprod_name(String prod_name){
                    return find.where().eq("prod_name", prod_name).findUnique();
                 }
                 
                public static Product findByprod_id(Long prod_id){
                    return find.where().eq("prod_id", prod_id).findUnique();
                }
                
                public static Product findByProdGenre(String genre){
                    return find.where().eq("genre",genre).findUnique();
                }
                
                public static double priceById(Long pid){
                    Product p = find.where().eq("prod_id",pid).findUnique();
                    return p.price;
                }
                
                
                
                public double getRating(){
                    return(review_score/number_of_reviews);
                }
                
     
                
                public static Comparator<Product> prodCompare = new Comparator<Product>(){
                    public int compare(Product p1, Product p2){
                        Double rating1 = p1.getRating();
                        Double rating2 = p2.getRating();
                        
                        return rating2.compareTo(rating1);
                    }
                };
                        
                        
                        
                
                
                
                /* public static List<Product>findTopRating(){
                    List<Product> pList = Product.findAll();
                    List<Product> returnList = new ArrayList<Product>();
                    for(int i = 0; i < pList.size(); i++){
                     if(pList.get(i).number_of_reviews != 0){
                         if(returnList.size() == 0){
                             returnList.add(pList.get(i));
                         }
                         
                         else{
                              if(pList.get(i).getRating() > pList.get(i-1).getRating()){
                                  System.out.println("AT "+i+" : "+pList.get(i).getName());
                                  System.out.println("AT "+(i-1)+" : "+pList.get(i-1).getName());
                             returnList.add((i-1), pList.get(i));
                             
                         }   
                              else{returnList.add(pList.get(i));}
                         }
                         
                     }
                     }
                    return returnList;
                 }*/
                
                public static List<Product>findTopRating(){
                   /* List<Product> pList = Product.findAll();
                    List<Product> returnList = new ArrayList<Product>();
                    for(int i = 0; i < pList.size(); i++){
                     if(pList.get(i).number_of_reviews != 0){
                         returnList.add(pList.get(i));
                                 }
                    }
                    Collections.sort(returnList, Product.prodCompare);
                   */ //return returnList;
                    return find.where().ne("review_score",0).orderBy("review_score/number_of_reviews desc").setMaxRows(10).findList();
                    
                    
                }
                  
                
                public static List<Product>saleProducts(){
                     return find.where().gt("sale_price",0 ).findList();
                 }
                
               
                    
                
               public static List<Product> getProdFromOrderDetails(List<OrderDetails> orderList){
                    List<Product> l = new ArrayList<Product>();
                   for(int i = 0; i < orderList.size(); i++){
                   Product p = find.where().eq("prod_id", orderList.get(i).getProductId()).findUnique();
                   l.add(p);
                    }
                   return l;
               }
               
                public static List<Product> getProdForWishlist(List<WishListItem> wishList){
                    List<Product> l = new ArrayList<Product>();
                   for(int i = 0; i < wishList.size(); i++){
                   Product p = find.where().eq("prod_id", wishList.get(i).getProductId()).findUnique();
                   l.add(p);
                    }
                   return l;
               }
                
                public static void increaseStock(Long pid, int stockIn){
                     Product p = findByprod_id(pid);
                     p.stock_level += stockIn;                             
                     p.save();
                }
               
               public void reduceStock(int quantity){                  
                  stock_level = stock_level - quantity;
                  System.out.println(stock_level);
                  
               }
               
               public static void reduceStockFromOrderDetails(List<OrderDetails> od){
                   for(int i = 0; i < od.size(); i++){
                       Product p = find.where().eq("prod_id",od.get(i).getProductId()).findUnique();
                       p.reduceStock(od.get(i).getQty());
                       p.save();
                   }
               }
               
               public int getNumberOfReviews(){
                   return number_of_reviews;
               }
               
               public void incrementNumberOfReviews(){
                   number_of_reviews = number_of_reviews + 1;
               }
               
               public void increaseReviewScore(int score){
                   review_score = review_score + score;
               }
               
               public void setSalePrice(double p){
                   sale_price = p;
               }
               
               public double getSalePrice(){
                   return sale_price;
               }
               
               public void setPrice(double p){
                   price = p;
               }
               
               public double getPrice(){
                   return price;
               }
               
               public String getName(){
               return prod_name;
               }
               public Long getId(){
               return prod_id;
               }
               public int getStockLevel(){
                   return stock_level;
               }
               
               public String getPlatform(){
               return platform;
               }
               
               
}