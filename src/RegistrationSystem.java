
/*
 * Created by: Andrew Nguyen
 * Date: 2019-05-08
 * Time: 14:40
 * DatabaseProgramming
 */

import java.sql.*;

public class RegistrationSystem {

    private String userid;
    private String pass;
    private String connStr;

    public RegistrationSystem(String userid, String pass) {
        this.userid = userid;
        this.pass = pass;
        connStr = "jdbc:mysql://mysql.cs.wwu.edu:3306/" + userid + "?useSSL=false";
    }

    public void run() {

        /**
         * 1) prompt user for student ID #
         * 2) Prompt user for different operations
         *      - Get Transcript
         *      - Check Degree
         *      - Add Course
         *      - Remove Course
         *      -Exit
         *
         */

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Attempting connection...");
            Connection con = DriverManager.getConnection(connStr, userid, pass);
            System.out.println(con.isClosed());

        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } catch (ClassNotFoundException classEx) {
            classEx.printStackTrace();
        }
    }

    private String getTranscript() {
        return "";
    }

    private String checkDegree() {
        return "";
    }

    private String addCourse(int courseNum) {
        return "";
    }

    private String removeCourse(int courseNum) {
        return "";
    }

    private void exit() {
        System.exit(0);
    }

}