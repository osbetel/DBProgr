/*
 * Created by: Andrew Nguyen
 * Date: 2019-05-12
 * Time: 18:48
 * DBProgr
 */

import java.sql.*;

public class Student {

    private Connection dbcon;

    private String ID;
    private String name;
    private String dept_name;
    private int tot_cred;


    public Student(String id, Connection con) {
        dbcon = con;
        ID = id;
        name = getName();
        dept_name = getDept();
        tot_cred = getCredits();
    }

    public void getTranscript() {
        System.out.println("gaaaaay");
    }

    public void checkDegree() {
        System.out.println("gaaaaay");
    }

    public void addCourse(String courseNum) {
        System.out.println("gaaaaay");
    }

    public void removeCourse(String courseNum) {
        System.out.println("gaaaaay");
    }

    public String executeStatement(PreparedStatement exec) throws SQLException{
        exec.setString(1, ID);
        ResultSet rs = exec.executeQuery();
        rs.next();
        return rs.getString(1);
    }

    public String getName() {
        try {
            PreparedStatement exec = dbcon.prepareStatement("SELECT name FROM student WHERE id=?");
            exec.setString(1, ID);
            return executeStatement(exec);
        } catch (SQLException sqlEx) {
            System.out.println("SQL query is invalid.");
        }
        return null;
    }

    public String getDept() {
        try {
            PreparedStatement exec = dbcon.prepareStatement("SELECT dept_name FROM student WHERE id=?");
            exec.setString(1, ID);
            return executeStatement(exec);
        } catch (SQLException sqlEx) {
            System.out.println("SQL query is invalid.");
        }
        return null;
    }

    public int getCredits() {
        try {
            PreparedStatement exec = dbcon.prepareStatement("SELECT tot_cred FROM student WHERE id=?");
            exec.setString(1, ID);
            return Integer.parseInt(executeStatement(exec));
        } catch (SQLException sqlEx) {
            System.out.println("SQL query is invalid.");
        }
        return -1;
    }
}
