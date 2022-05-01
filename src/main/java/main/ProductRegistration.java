package main;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@WebServlet("/ProductRegistration")
public class ProductRegistration extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            HttpSession session = req.getSession();
            int prId = Integer.parseInt(req.getParameter("prID"));
            String prName = (String) (req.getParameter("prName"));
            String category = (String) (req.getParameter("ProductCategory"));
            float prPrice = Float.parseFloat(req.getParameter("prPrice"));
            PrintWriter out = resp.getWriter();
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            String oracleUrl = "jdbc:oracle:thin:@localhost:1521/xe";
            Connection con = DriverManager.getConnection(oracleUrl, "system", "oracle");
            String TableCreation = "create table PRODUCTDETAILS("
                    + "uniqueID int GENERATED ALWAYS as IDENTITY (START with 1 INCREMENT by 1) primary key,"
                    + "ProductID int,"
                    + "ProductName varchar(100),"
                    + "Category varchar(100),"
                    + "ProductPrice float(10),"
                    + "pdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
            Statement stmt = con.createStatement();
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet table = dbm.getTables(null, null, "PRODUCTDETAILS", null);
            if (!table.next())
                stmt.execute(TableCreation);
            if (req.getParameter("Register") != null)
            {
                PreparedStatement stmt1 = con.prepareStatement("insert into PRODUCTDETAILS(ProductID, ProductName, Category, ProductPrice) values (?, ?, ?, ?)");
                stmt1.setInt(1, prId);
                stmt1.setString(2, prName);
                stmt1.setString(3, category);
                stmt1.setFloat(4, prPrice);
                stmt1.executeUpdate();
                out.append("<script>alert(\"records have been inserted\");</script>");
                req.getRequestDispatcher("index.html").include(req, resp);
            }
            if (req.getParameter("Search") != null)
            {
                req.getRequestDispatcher("CartServlet.java").include(req, resp);
            }
            con.close();
//            session.invalidate();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}