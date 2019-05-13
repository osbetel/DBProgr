/*
 * Created by: Andrew Nguyen
 * Date: 2019-05-12
 * Time: 18:48
 * DBProgr
 */

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Student {

    private Connection dbcon;

    private String ID;
    private String name;
    private String dept_name;
    private int tot_cred;

    private HashMap<String, String> transcript;


    public Student(String id, Connection con) {
        dbcon = con;
        ID = id;
        name = getName();
        dept_name = getDept();
        tot_cred = getCredits();

        transcript = new HashMap<>();
    }


    public void getTranscript() throws SQLException {
        PreparedStatement exec = dbcon.prepareStatement("SELECT name, student.ID, takes.course_id, course.title, semester, year, grade, credits FROM student, course, takes \n" +
                "WHERE student.ID=takes.ID AND student.ID = ? AND takes.course_id=course.course_id ORDER BY semester, year");
        exec.setString(1, ID);
        ResultSet rs = exec.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colIndex = rsmd.getColumnCount();

        ArrayList<String> grades = new ArrayList<>();
        ArrayList<String> credits = new ArrayList<>();
        System.out.println(" Name: " + name + ", ID: " + ID);

        while (rs.next()) {
            String course = null, semester = null, year = null, grade = null, title = null, cred = null;
            for (int i = 1; i <= colIndex; i++) {
                String key = rsmd.getColumnName(i);
                String val = rs.getString(i);

                switch (key) {
                    case "course_id":
                        course = val;
                        break;
                    case "title":
                        title = val;
                    case "semester":
                        semester = val;
                        break;
                    case "year":
                        year = val;
                        break;
                    case "grade":
                        grade = val;
                        break;
                    case "credits":
                        cred = val;
                        break;
                }
            }
            String s = String.format("%7s%30s%8s%6s%3s%3s", course, title, semester, year, grade, cred);
            grades.add(grade);
            credits.add(cred);
            System.out.println(s);
        }
        String gpa = String.format("%57s", "GPA: " + calcGPA(grades, credits));
        System.out.println(gpa);
    }


    /**
     * gpa formula is (letter grade value) * (# of credits in the class) = quality points.
     *     Cumulative gpa = quality points / total credits
     */
    private String calcGPA(ArrayList<String> grades, ArrayList<String> credits) {
        double qpts = 0;
        double totalcr = 0;
        for (int i = 0; i < grades.size(); i++) {
            double cr = Integer.parseInt(credits.get(i));
            double gr = 0;
            switch (grades.get(i)) {
                case "A":
                    gr = 4.0;
                    break;
                case "A-":
                    gr = 3.7;
                    break;
                case "B+":
                    gr = 3.3;
                    break;
                case "B":
                    gr = 3.0;
                    break;
                case "B-":
                    gr = 2.7;
                    break;
                case "C+":
                    gr = 2.3;
                    break;
                case "C":
                    gr = 2.0;
                    break;
                case "C-":
                    gr = 1.7;
                    break;
                case "D+":
                    gr = 1.3;
                    break;
                case "D":
                    gr = 1.0;
                    break;
                case "D-":
                    gr = 0.7;
                    break;
                default:
                    gr = 0.0;
                    break;
            }

            qpts += (gr * cr);
            totalcr += cr;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(qpts / totalcr);
    }


    /**
     * This needs to get a list of courses in the student's department, that is not in their list of taken courses
     */
    public void checkDegree() throws SQLException {
        //all courses the student needs, minus courses they've taken
        PreparedStatement exec = dbcon.prepareStatement("SELECT * from course where course.dept_name = ? " +
                                             "and course.course_id NOT IN (select course_id from takes where ID = ?)");
        exec.setString(1, dept_name);
        exec.setString(2, ID);
        ResultSet rs = exec.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colIndex = rsmd.getColumnCount();

        System.out.println(" Name: " + name + ", ID: " + ID);

        while (rs.next()) {
            String course = null, title = null, cred = null;
            for (int i = 1; i <= colIndex; i++) {
                String key = rsmd.getColumnName(i);
                String val = rs.getString(i);

                switch (key) {
                    case "course_id":
                        course = val;
                        break;
                    case "title":
                        title = val;
                        break;
                    case "credits":
                        cred = val;
                        break;
                }
            }
            System.out.printf("%7s%30s%9s", "Course", "Title", "Credits");
            System.out.println();
            String s = String.format("%7s%30s%9s", course, title, cred);
            System.out.println(s);
        }
    }

    /**
     * This should add a course based on course_id, like "CS-191", and should also check to see if this person
     * has already taken CS-191, and whether they have the prereqs for it. If yes then insert.
     */
    public void addCourse(String courseID) {
        System.out.println("gaaaaay");
    }


    public void removeCourse(String courseID) {
        
    }


    public String getName() {
        try {
            PreparedStatement exec = dbcon.prepareStatement("SELECT name FROM student WHERE id=?");
            exec.setString(1, ID);
            ResultSet rs = exec.executeQuery();
            rs.next();
            return rs.getString(1);
        } catch (SQLException sqlEx) {
            System.out.println("SQL query is invalid.");
        }
        return null;
    }


    public String getDept() {
        try {
            PreparedStatement exec = dbcon.prepareStatement("SELECT dept_name FROM student WHERE id=?");
            exec.setString(1, ID);
            ResultSet rs = exec.executeQuery();
            rs.next();
            return rs.getString(1);
        } catch (SQLException sqlEx) {
            System.out.println("SQL query is invalid.");
        }
        return null;
    }


    public int getCredits() {
        try {
            PreparedStatement exec = dbcon.prepareStatement("SELECT tot_cred FROM student WHERE id=?");
            exec.setString(1, ID);
            ResultSet rs = exec.executeQuery();
            rs.next();
            return Integer.parseInt(rs.getString(1));
        } catch (SQLException sqlEx) {
            System.out.println("SQL query is invalid.");
        }
        return -1;
    }


}
