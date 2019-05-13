/*
 * Created by: Andrew Nguyen
 * Date: 2019-05-12
 * Time: 18:48
 * DBProgr
 */

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Student {

    private Connection dbcon;

    private String ID;
    private String name;
    private String dept_name;
    private int tot_cred;

    /**
     * Constructs a student object
     * @param id ID number as a string, passed in from RegistrationSystem
     * @param con Connection object for this student's open session
     */
    public Student(String id, Connection con) {
        dbcon = con;
        ID = id;
        name = getName();
        dept_name = getDept();
        tot_cred = getCredits();
    }


    /**
     * Fetches and prints the student's transcript
     */
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

//        System.out.println(" Name: " + name + ", ID: " + ID);
        System.out.printf("%7s%30s%9s", "Course", "Title", "Credits");
        System.out.println();
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
            String s = String.format("%7s%30s%9s", course, title, cred);
            System.out.println(s);
        }
    }


    /**
     * This should add a course based on course_id, like "CS-191", and section, semester, and year data.
     */
    public void addCourse() throws SQLException {
        listCoursesAvail();

        Scanner sc = new Scanner(System.in);
        String[] in = null;
        while (in == null) {
            try {
                System.out.print("Which course would you like to add? Enter the course, section, semester, year, and final grade," +
                        "separated by commas (eg: \"CS-347, 1, Fall, 2009, A-\"): ");
                in = sc.nextLine().split(",");
                if (in.length != 5) throw new Exception();
            } catch (Exception ex) {
                in = null;
                System.out.println("Please input the data in the requested format.");
            }
        }

        String course = in[0];
        String section = in[1];
        String semester = in[2];
        String year = in[3];
        String grade = in[4];

        try {
            PreparedStatement exec = dbcon.prepareStatement("INSERT INTO takes (ID, course_id, sec_id, semester, year, grade) VALUES (?,?,?,?,?,?)");
            exec.setString(1, ID);
            exec.setString(2, course);
            exec.setString(3, section);
            exec.setString(4, semester);
            exec.setString(5, year);
            exec.setString(6, grade);
            exec.executeQuery();
        } catch (SQLException sqlEx) {
            System.out.println("Course could not be inserted. Please verify your input matched the described formats, or that the course is already added.");
        }
    }

    /**
     * Lists courses that the student has taken, and queries to remove one of them
     */
    public void removeCourse() throws SQLException {
        // 1) prompt for semester
        // 2) prompt for year
        listCoursesTaking();
        Scanner sc = new Scanner(System.in);
        String[] in = null;
        while (in == null) {
            try {
                System.out.print("Which course would you like to remove? Enter the course, section, semester, and year," +
                        "separated by commas (eg: \"CS-347, 1, Fall, 2009\"): ");
                in = sc.nextLine().split(",");
                if (in.length != 4) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                in = null;
                System.out.println("Please input the data in the requested format.");
            }
        }

        String course = in[0];
        String section = in[1];
        String semester = in[2];
        String year = in[3];

        try {
            PreparedStatement exec = dbcon.prepareStatement("DELETE FROM takes WHERE ID = ? AND course_id = ? AND sec_id = ? AND semester = ? AND year = ?");
            exec.setString(1, ID);
            exec.setString(2, course);
            exec.setString(3, section);
            exec.setString(4, semester);
            exec.setString(5, year);
            exec.executeQuery();
        } catch (SQLException sqlEx) {
            System.out.println("Course could not be deleted. Please verify your input matched the described formats.");
        }
    }

    /**
     * Lists the courses a student is taking (takes table)
     */
    private void listCoursesTaking() throws SQLException {
        PreparedStatement exec = dbcon.prepareStatement("SELECT * FROM takes WHERE ID = ? ORDER BY year");
        exec.setString(1, ID);
        ResultSet rs = exec.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colIndex = rsmd.getColumnCount();

        System.out.println("Here are the courses for: " + name +", ID: " + ID);
        System.out.printf("%7s%10s%10s%7s", "Course", "Section", "Semester", "Year");
        System.out.println();
        while (rs.next()) {
            String course = null, section = null, semester = null, year = null;
            for (int i = 1; i <= colIndex; i++) {
                String key = rsmd.getColumnName(i);
                String val = rs.getString(i);

                switch (key) {
                    case "course_id":
                        course = val;
                        break;
                    case "sec_id":
                        section = val;
                        break;
                    case "semester":
                        semester = val;
                        break;
                    case "year":
                        year = val;
                        break;
                }
            }
            String s = String.format("%7s%10s%10s%7s", course, section, semester, year);
            System.out.println(s);
        }
    }


    /**
     * Lists all courses available in the db
     */
    private void listCoursesAvail() throws SQLException {

        PreparedStatement exec = dbcon.prepareStatement("SELECT * FROM teaches ORDER BY course_ID");
        ResultSet rs = exec.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int colIndex = rsmd.getColumnCount();

        System.out.println("Here is the catalog of courses you can add to this student's record: ");
        System.out.printf("%7s%10s%10s%7s", "Course", "Section", "Semester", "Year");
        System.out.println();
        while (rs.next()) {
            String course = null, section = null, semester = null, year = null;
            for (int i = 1; i <= colIndex; i++) {
                String key = rsmd.getColumnName(i);
                String val = rs.getString(i);

                switch (key) {
                    case "course_id":
                        course = val;
                        break;
                    case "sec_id":
                        section = val;
                        break;
                    case "semester":
                        semester = val;
                        break;
                    case "year":
                        year = val;
                        break;
                }
            }
            String s = String.format("%7s%10s%10s%7s", course, section, semester, year);
            System.out.println(s);
        }
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
