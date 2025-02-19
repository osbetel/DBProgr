
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
        connStr = "jdbc:mysql://mysql.cs.wwu.edu:3306/" + userid + "?useSSL=false";
//        connStr = "jdbc:mysql://localhost:3306/" + userid + "?useSSL=false"; //For ssh tunneling only

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
            System.out.print("Please enter a command (type \"help\" for options): ");
            while (true) {
                String command = sc.next();

                switch (command.toLowerCase()) {
                    case "get-transcript":
                        student.getTranscript();
                        break;

                    case "check-degree":
                        student.checkDegree();
                        break;

                    case "add-course":
                        student.addCourse();
                        break;

                    case "remove-course":
                        student.removeCourse();
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

    /**
     * Query the user for ID number
     */
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
}