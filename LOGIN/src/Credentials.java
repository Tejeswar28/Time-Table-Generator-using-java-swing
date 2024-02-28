/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author harsh
 */
import java.util.*;
import java.sql.*;
public class Credentials {
    public static void main(String[] args) throws Exception{
        String url="jdbc:mysql://localhost:3306/tejes?useSSL=false";
        String uname="root";
        String pass="wartejes28";
        //String query="INSERT into student values(?,?);";
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn=DriverManager.getConnection(url,uname,pass);
       // PreparedStatement st=conn.prepareStatement(query);
        Statement st=conn.createStatement();
        Scanner sc=new Scanner(System.in);
        int n;
        n=sc.nextInt();
           String sr;
          sr=sc.next();
        /*for(int i=0;i<n;i++){
          int n1;
          n1=sc.nextInt();
       
        String s;
        s=sc.next();
        st.setInt(1, n1);
        st.setString(2, s);
        st.executeUpdate(query);
        }
       */
        ResultSet rs=st.executeQuery("select *from student where f="+n+" AND g="+sr+"");
        while(rs.next())
        {
            System.out.println(rs.getInt(1)+" "+rs.getString(2));
        }
        st.close();
        rs.close();
        
        
    }
}


