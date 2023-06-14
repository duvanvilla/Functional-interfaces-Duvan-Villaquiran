package com.sales.functional;

import com.sales.functional.database.Database;
import com.sales.functional.entities.Product;
import com.sales.functional.entities.Sale;

import java.time.ZoneId;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import java.util.stream.Collectors;

public class SuppliesFunctional {
    static ArrayList<Sale> sales = Database.loadDatabase();
    public static void main(String[] args) {
        loadMenu();
    }

    /** 1. Obtenga todas las ventas(Sale) que tengan como método de compra(Purchase method) 'Online'

        2. Obtenga todas las ventas(Sale) que tengan como ubicación New York y filtre también validando si las ventas fueron con cupón o sin cupón

        3. Obtenga la cantidad de ventas en las que los clientes usaron cupón

        4. Obtenga todas las ventas que fueron realizadas un año específico 'YYYY'

        5. Obtenga el número de ventas en donde el indicador de satisfacción es menor a 4.

        6. Calcule el monto total que pagó el cliente en cada venta.

        7. Obtenga todas las ventas en las que el comprador es una mujer y fue comprado en la tienda ('in store')

        8. Obtenga el número de productos comprados por todos los clientes segmentándolos por etiquetas(tags)

        9. Obtenga cuantos hombres usaron cupón y cuantas mujeres usaron cupón;

        10. Obtenga la venta con la compra más costosa y la venta con la compra más barata
     */

    public static void menu(){
        System.out.println("Supplies sales");
        System.out.println("1. Compras en linea");
        System.out.println("2. Compras realizadas en New York con o sin cupón");
        System.out.println("3. el numero de ventas en donde se usaron cupones y en el numero en las que no");
        System.out.println("4. Ventas realizadas en el año YYYY");
        System.out.println("5. Ventas en donde el indicador de satisfacción es menor a N");
        //TO DO:
        System.out.println("6. Monto total pagado en cada venta");
        System.out.println("7. Ventas en donde compró una mujer en la tienda(in store)");
        System.out.println("8. Agrupación de productos por etiquetas(tags)");
        System.out.println("9. Cuantos hombres y mujeres usaron cupón");
        System.out.println("10. Venta con mayor costo y menor costo");
    }

    public static void loadMenu(){
        Scanner sc = new Scanner(System.in);
        menu();
        System.out.print("Type option: ");
        String op=sc.nextLine();
        switch(op){
            case "1":
                getOnlinePurchases();
                break;
            case "2":
                System.out.print("¿quiere filtrar las ventas que usaron cupón? Y/N: ");
                getNySales(sc.nextLine());
                break;
            case "3":
                couponUsage();
                break;
            case "4":
                System.out.print("Cual es el año por el que quiere filtrar: ");
                salesByYear(sc.nextLine());
                break;
            case "5":
                System.out.print("Cual es el numero de satisfacción por que quiere filtrar (1-5): ");
                salesBySatisfaction(sc.nextLine());
                break;
            case "6":
                getCostTotalByEachSale();
                break;
            case "7":
                getSaleToWomenInStore();
                break;
            case "8":
                salesGroupByTag();
                break;
            case "9":
                amountCouponByGender();
                break;
            case "10":
                saleMostCostMinusCost();
                break;
            default:
                System.out.println("ERROR en el input, este metodo no ha sido creado. Intente de nuevo");
        }

    }

    //Opc 1
    public static void getOnlinePurchases(){
        Predicate<Sale> onlinePurchased = sale -> sale.getPurchasedMethod().equals("Online");
        ArrayList<Sale> result = sales.stream().filter(onlinePurchased).collect(Collectors.toCollection(ArrayList::new));
        result.forEach(System.out::println);

    }

    //Opc 2
    public static void getNySales(String inCoupon){
        Predicate<Sale> couponUsage = sale -> sale.getCouponUsed().equals(inCoupon.equalsIgnoreCase("Y"))
                && sale.getLocation().equals("New York");
        ArrayList<Sale> result = sales.stream().filter(couponUsage).collect(Collectors.toCollection(ArrayList::new));
        result.forEach(System.out::println);

    }

    //Opc 3
    public static void couponUsage(){
        Predicate<Sale> couponUsage = Sale::getCouponUsed;
        Predicate<Sale> couponNoUsage = sale -> !sale.getCouponUsed();
        Map<String,Long> usage  = Map.of("Usage", sales.stream().filter(couponUsage).count(), "Not usage", sales.stream().filter(couponNoUsage).count());

        usage.forEach((key,value)-> System.out.println(key+": "+value));

    }

    //Opc 4
    public static void salesByYear(String inYear){
        Function<Sale,String> getYear = sale -> String.valueOf(sale.getSaleDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        ArrayList<Sale> salesByYYYY = sales.stream().filter(sale -> getYear.apply(sale).equals(inYear)).collect(Collectors.toCollection(ArrayList::new));
        salesByYYYY.forEach(System.out::println);
    }

    //Opc 5
    public static void salesBySatisfaction(String inSatis){
        Consumer<String> satisfaction = satis -> sales.stream().filter(sale -> sale.getCustomer().getSatisfaction().toString().equals(satis)).collect(Collectors.toCollection(ArrayList::new)).forEach(System.out::println);
        satisfaction.accept(inSatis);
    }

    //Opc 6
    public static void getCostTotalByEachSale (){
        List<Sale> list = new ArrayList<>();
        for (Sale sale : sales) {
            double totalSale = sale.getItems().stream().mapToDouble(Product::getPrice).sum();
            sale.setTotal(totalSale);
            list.add(sale);
        }
        list.forEach(sale -> System.out.println("Cliente " + sale.getCustomer() + ", " + "total de la venta: "
                + sale.getTotal()));
    }

    //Opc 7
    public static void getSaleToWomenInStore(){
        Predicate<Sale> saleToWomen = sale -> sale.getCustomer().getGender().equalsIgnoreCase("F")
                && sale.getPurchasedMethod().equalsIgnoreCase("In store");
        ArrayList<Sale> response = sales.stream().filter(saleToWomen).collect(Collectors.toCollection(ArrayList::new));
        response.forEach(System.out::println);
    }

    //Opc 8
    private static void salesGroupByTag() {
        Map<String, Set<String>> mapTagsProduct = sales.stream()
                .flatMap(sale -> sale.getItems().stream())
                .flatMap(item -> item.getTags().stream()
                        .map(tag -> new AbstractMap.SimpleEntry<>(tag, item.getName())))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));

        System.out.println(mapTagsProduct);
    }

    //Opc 9
    public static void amountCouponByGender() {
        Predicate<Sale> couponUseMen = sale -> sale.getCustomer().getGender().equalsIgnoreCase("M") && sale.getCouponUsed();
        Predicate<Sale> couponUseWomen = sale -> sale.getCustomer().getGender().equalsIgnoreCase("F") && sale.getCouponUsed();
        int amountTotalCouponMen = (int) sales.stream().filter(couponUseMen).count();
        int amountTotalCouponWomen = (int) sales.stream().filter(couponUseWomen).count();
        System.out.println("Total number of coupons used by men : " + amountTotalCouponMen);
        System.out.println("Total number of coupons used by women : " + amountTotalCouponWomen);
    }

    //Opc 10
    public static List<Sale> getSales(List<Sale> sales) {
        List<Sale> list = new ArrayList<>();
        for (Sale sale : sales) {
            double totalSale = sale.getItems().stream().mapToDouble(Product::getPrice).sum();
            sale.setTotal(totalSale);
            list.add(sale);
        }
        return list;
    }

    public static void saleMostCostMinusCost() {
        List<Sale> updatedSales = getSales(sales);
        Optional<Double> saleMostCost = updatedSales.stream().map(Sale::getTotal).max(Comparator.naturalOrder());
        Optional<Double> saleMinusCost = updatedSales.stream().map(Sale::getTotal).min(Comparator.naturalOrder());
        double saleMostCostInteger = saleMostCost.orElse(0.0);
        double saleMinusCostInteger = saleMinusCost.orElse(0.0);
        System.out.println("Sale most cost is: " + saleMostCostInteger);
        System.out.println("Sale minus cost is: " + saleMinusCostInteger);
    }
}
