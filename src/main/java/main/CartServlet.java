package main;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CartServlet extends HttpServlet
{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doGet(req, resp);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            PrintWriter out = resp.getWriter();
//            req.getSession(false);
            req.setAttribute("clear", req.getParameter("clear"));
            req.setAttribute("prNamesr", req.getParameter("prNamesr"));
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            String oracleUrl = "jdbc:oracle:thin:@localhost:1521/xe";
            Connection con = DriverManager.getConnection(oracleUrl, "system", "oracle");
            System.out.println("Connection established......");
            Statement stmt = con.createStatement();
            FileReader fr = new FileReader(req.getRealPath("List.html"));
            BufferedReader br = new BufferedReader(fr);
            StringBuilder html = new StringBuilder("");
            String line;
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet table = dbm.getTables(null, null, "PRODUCTDETAILS", null);
            ResultSet rs =null;
            if (table.next())
            {
                rs = stmt.executeQuery("Select * from PRODUCTDETAILS");
            }
            if (req.getAttribute("clear") != null)
            {
                stmt.executeQuery("delete from PRODUCTDETAILS");
                rs = stmt.executeQuery("Select * from PRODUCTDETAILS");
                req.removeAttribute("clear");
            } else if (req.getAttribute("prNamesr") != null)
            {
                int id = Integer.parseInt(req.getParameter("prNamesr"));
                rs = stmt.executeQuery("Select * from PRODUCTDETAILS where UNIQUEID = " + id);
                req.removeAttribute("prNamesr");
            }
            while ((line = br.readLine()) != null)
            {
                html.append(line);
                html.append("\n");
                if (line.contains("<!--Begin-->") && rs != null)
                {
                    while (rs.next())
                    {
                        html.append("\n<tr>\n");
                        html.append("<td>" + rs.getInt("UNIQUEID") + "</td>\n");
                        html.append("<td>" + rs.getInt("PRODUCTID") + "</td>\n");
                        html.append("<td>" + rs.getString("PRODUCTNAME") + "</td>\n");
                        html.append("<td>" + rs.getString("CATEGORY") + "</td>\n");
                        html.append("<td>" + rs.getFloat("PRODUCTPRICE") + "</td>\n");
                        html.append("<td>" + rs.getString("PDATE") + "</td>\n");
                        html.append("</tr>\n");
                    }
                }
            }
            out.println(html.toString());
            rs.close();
            stmt.close();
            out.close();
            con.close();
//            System.out.println(html);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (SQLException ex)
        {
            Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}