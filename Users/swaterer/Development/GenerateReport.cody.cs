using System.Collections.Generic;

public class GenerateReportRefactored 
{
    public void GenerateReport(List<Order> orders, List<Product> products)  
    {    
        Report report = new Report();  

        foreach (Order order in orders) 
        {
            Product product = GetProductForOrder(order, products);  

            if (product != null) 
            {
                double revenue = CalculateRevenueForOrder(order, product);  

                if (revenue > 1000) 
                {
                    Customer customer = GetCustomer(order.CustomerId);  

                    if (customer.HasDiscount) 
                    {
                        revenue = ApplyDiscount(revenue, customer.DiscountPercentage);  
                    }

                    report.AddRevenue(revenue);  
                }
            }
        }

        report.Print();
    }

    private Product GetProductForOrder(Order order, List<Product> products) 
    {
    }

    private double CalculateRevenueForOrder(Order order, Product product) 
    {
        return order.Quantity * product.Price;  
    }
}

public void GenerateReport(List<Order> orders, List<Product> products)  
{    
    Report report = new Report();  

    foreach (Order order in orders) 
    {
        Product product = GetProductForOrder(order, products);  

        if (product != null) 
        {
            double revenue = CalculateRevenueForOrder(order, product);  

            if (revenue > 1000) 
            {
                Customer customer = GetCustomer(order.CustomerId);  

                if (customer.HasDiscount) 
                {
                    revenue = ApplyDiscount(revenue, customer.DiscountPercentage);  
                }

                report.AddRevenue(revenue);  
            }
        }
    }

    report.Print();
}

private Product GetProductForOrder(Order order, List<Product> products) 
{
}

private double CalculateRevenueForOrder(Order order, Product product) 
{
    return order.Quantity * product.Price;  
}

using System.Collections.Generic;

public class GenerateReportRefactored 
{
    public void GenerateReport(List<Order> orders, List<Product> products)  
    {    
        Report report = new Report();  

        foreach (Order order in orders) 
        {
            Product product = GetProductForOrder(order, products);  

            if (product != null) 
            {
                double revenue = CalculateRevenueForOrder(order, product);  

                if (revenue > 1000) 
                {
                    Customer customer = GetCustomer(order.CustomerId);  

                    if (customer.HasDiscount) 
                    {
                        revenue = ApplyDiscount(revenue, customer.DiscountPercentage);  
                    }

                    report.AddRevenue(revenue);  
                }
            }
        }

        report.Print();
    }

    private Product GetProductForOrder(Order order, List<Product> products) 
    {
        return products.SingleOrDefault(p => p.Id == order.ProductId);
    }

    private double CalculateRevenueForOrder(Order order, Product product) 
    {
        return order.Quantity * product.Price;  
    }

    private Customer GetCustomer(int customerId)
    {
        // logic to get customer from customerId
    }

    private double ApplyDiscount(double revenue, double discountPercentage)
    {
        return revenue * (1 - discountPercentage);
    }
}

