package com.sales.functional.database;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.sales.functional.entities.Customer;
import com.sales.functional.entities.Product;
import com.sales.functional.entities.Sale;
import org.bson.Document;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Database {

    public static ArrayList<Sale> loadDatabase(){
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        //I stablish the connection
        MongoClient mongoClient = MongoClients
                .create("mongodb+srv://santiagoposadag:Admin8080@empleado-restful.w5uje.mongodb.net/");
        // I'm getting the table/collection that i want to use
        MongoCollection<Document> collection = mongoClient.getDatabase("sample_supplies").getCollection("sales");
        //I'm saving all the data from the previous line into an ArrayList of Document
        ArrayList<Document> data= collection.find().into(new ArrayList<>());

        return fillData(data);
    }

    public static ArrayList<Sale> fillData(ArrayList<Document> data){

        ArrayList<Sale> sales = new ArrayList<>();

        Function<Document, List<Product>> loadProducts= (document) -> {
            List<Document> items = document.getList("items",Document.class);
            List<Product> products = new ArrayList<>();
            items.forEach(item -> {
                Product p = new Product();
                p.setName(item.getString("name"));
                p.setPrice(item.get("price",Number.class).doubleValue());
                p.setQuantity(item.getInteger("quantity"));
                p.setTags(item.getList("tags",String.class));
                products.add(p);

            });
            return products;
        };

        Function<Document, Customer> loadCustomer = (document) ->{
            Customer c = new Customer();
            Document docCustomer = (Document) document.get("customer");
            c.setGender(docCustomer.getString("gender"));
            c.setAge(docCustomer.getInteger("age"));
            c.setEmail(docCustomer.getString("email"));
            c.setSatisfaction(docCustomer.getInteger("satisfaction"));
            return c;
        };

        Consumer<ArrayList<Document>> generateSales = dataSales -> {
            dataSales.forEach(document -> {
                Sale sale = new Sale();

                sale.setSaleDate(document.getDate("saleDate"));
                sale.setItems(loadProducts.apply(document));
                sale.setLocation(document.getString("storeLocation"));
                sale.setCustomer(loadCustomer.apply(document));
                sale.setCouponUsed(document.getBoolean("couponUsed"));
                sale.setPurchasedMethod(document.getString("purchaseMethod"));

                sales.add(sale);


            });

        };

        generateSales.accept(data);


        return sales;





    }

    //TO DO: Format output
}
