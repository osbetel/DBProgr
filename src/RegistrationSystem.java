
/*
 * Created by: Andrew Nguyen
 * Date: 2019-05-08
 * Time: 14:40
 * DatabaseProgramming
 */

import java.sql.*;
import java.util.Scanner;

public class RegistrationSystem {

    private String userid;
    private String pass;
    private String connStr;

    private String[] testIDs = {"98988", "00128", "12345", "54321", "76543",
            "76653", "98765", "19991", "55739", "44553", "45678", "70557"};

    public RegistrationSystem(String userid, String pass) {
        this.userid = userid;
        this.pass = pass;
//        connStr = "jdbc:mysql://mysql.cs.wwu.edu:3306/" + userid + "?useSSL=false";
        connStr = "jdbc:mysql://localhost:3306/" + userid + "?useSSL=false"; //For ssh tunneling only

    }

    public void run() {

        /**
         * 1) prompt user for student ID #
         * 2) Prompt user for different operations
         *      - Get Transcript
         *      - Check Degree
         *      - Add Course
         *      -Remove Course
         *      -Exit
         */

        Connection con = null;
        try {
            System.out.println("Attempting connection...");
            con = DriverManager.getConnection(connStr, userid, pass);
            System.out.println("Connection successful.\n");
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

        //After connection successful...
        // 1) Get ID num and make Student obj.
        Student student = new Student(requestIDNumber(), con);

        // 2) Prompt user for what the want to do
        Scanner sc = new Scanner(System.in);

        try {
//        student.getTranscript();
            student.checkDegree();
            System.exit(0);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            while (true) {
                System.out.print("Please enter a command (type \"help\" for options): ");
                String command = sc.next();

                String courseId;
                switch (command.toLowerCase()) {
                    case "get-transcript":
                        student.getTranscript();
                        break;

                    case "check-degree":
                        student.checkDegree();
                        break;

                    case "add-course":
                        System.out.print("Input the Course ID of the course to add: ");
                        courseId = sc.next();
                        System.out.println();
                        student.addCourse(courseId);
                        break;

                    case "remove-course":
                        System.out.print("Input the Course ID of the course to remove: ");
                        courseId = sc.next();
                        System.out.println();
                        student.removeCourse(courseId);
                        break;

                    case "exit":
                        System.exit(0);
                        break;

                    case "help":
                        System.out.println("Available commands: get-transcript, check-degree, " +
                                "add-course, remove-course, exit");
                        break;
                }
                System.out.println();
            }
        } catch (SQLException sqlEx) {
            System.out.println("Input invalid.");
        }
    }

    private String requestIDNumber() {

        String idnum = "-1";
        while (idnum.equalsIgnoreCase("-1")) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print("Please input a student ID number: ");
                idnum = sc.next();
                System.out.println();

                int k = Integer.parseInt(idnum);
                if (k > 99999999) {
                    throw new NumberFormatException("ID number is too long, or contains non-integer characters!");
                }

            } catch (NumberFormatException ex) {
                idnum = "-1";
                System.out.println("Please input a valid ID number: ");
            }
        }
        return idnum;
    }

    //Executes anything. Testing purposes only.
    private void executeSQL(String query, Connection con) {

        try {
            Statement exec = con.createStatement();
            ResultSet rs = exec.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int colIndex = rsmd.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= colIndex; i++) {
                    if (i > 1) System.out.print(",  ");
                    String val = rs.getString(i);
                    System.out.print(val + " " + rsmd.getColumnName(i));
                }
                System.out.println("");
            }
        } catch (SQLException sqlEx) {
            System.out.println("Executed statement invalid.");
        }
    }
}