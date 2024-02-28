/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author harsh
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import javax.swing.JOptionPane;
 
class ttgenerator {

    private HashMap<String, ArrayList<String>> coursesPC = new HashMap<>();
    private HashMap<String, ArrayList<String>> coursesUC = new HashMap<>();
    private HashMap<String, ArrayList<String>> coursesPE = new HashMap<>();
    private HashMap<String, ArrayList<String>> coursesUE = new HashMap<>();
    private HashMap<String,ArrayList<String>> prevCourseTakenData = new HashMap<>();
    private HashMap<String,HashMap<String,ArrayList<String>>> coursePreferenceData = new HashMap<>();
    private TreeMap<String, HashMap<String,String>> employees = new TreeMap<>((k1,k2)->{
        return k1.compareTo(k2);
    });
    private ArrayList<String> employeesWithExtraAdministrativeRole = new ArrayList<>();
    private ArrayList<String> wishlist = new ArrayList<>();
    private HashMap<Integer,HashMap<Integer,HashMap<String,String>>> timetable = new HashMap<>();
    private HashMap<String, String> randomEmployeeId = new HashMap<>();

    private String status="not initialized";


    public ttgenerator(
        ArrayList<String> wishlist,
        HashMap<String,HashMap<String,String>> employeeData,
        ArrayList<String>employeesWithExtraAdministrativeRole,
        HashMap<String,String> courses,
        HashMap<String,ArrayList<String>> prevCourseTakenData,
        HashMap<String,HashMap<String,ArrayList<String>>> coursePreferenceData
    ) {
        setWishlist(wishlist);
        setEmployeeData(employeeData);
        setExtraAdministrativeRole(employeesWithExtraAdministrativeRole);
        setCourses(courses);
        setPreviousCourseTakenData(prevCourseTakenData);
        setCoursePreferenceData(coursePreferenceData);
        for (int d=0;d<7;++d){
            timetable.put(d,new HashMap<>());
            for (int t=0;t<24;++t){
                timetable.get(d).put(t,new HashMap<>());
            }
        }

        status="initialized";
        generateTimetable();
        status="generated";
    }

    public HashMap<Integer,HashMap<Integer,HashMap<String,String>>> getTimetable(){
        return this.timetable;
    }

    private boolean setCourses(HashMap<String,String> courses){
        for (String key : courses.keySet()) {
            if (wishlist.contains(key)){
            if (courses.get(key).equals("PC")) {
                coursesPC.put(key, new ArrayList<>());
            } else if (courses.get(key).equals("UC")) {
                coursesUC.put(key, new ArrayList<>());
            } else if (courses.get(key).equals("PE")) {
                coursesPE.put(key, new ArrayList<>());
            } else if (courses.get(key).equals("UE")) {
                coursesUE.put(key, new ArrayList<>());
            }
            else{
                System.out.println("Invalid Course Type "+key+" "+courses.get(key));
                // System.exit(0);
                // return false;
            }
        }}
        return true;
    }

    private boolean setWishlist(ArrayList<String>wishlist){
        for (String s : wishlist) {
            this.wishlist.add(s);
        }
        return true;
    }
 
    private boolean setPreviousCourseTakenData(HashMap<String,ArrayList<String>> arg){
        for (String key : arg.keySet()) {
            Collections.sort(arg.get(key));
            prevCourseTakenData.put(key,arg.get(key));
        }
        return true;
    }
 
    private boolean setCoursePreferenceData( HashMap<String,HashMap<String,ArrayList<String>>> arg){
        this.coursePreferenceData = arg;
        for (String key : arg.keySet()) {
            if (arg.get(key).get("PC") != null) {
                for (String course : arg.get(key).get("PC")) {
                    if (coursesPC.containsKey(course)) {
                        coursesPC.get(course).add(key);
                    }
                }
            }
            else if (arg.get(key).get("UC") != null) {
                for (String course : arg.get(key).get("UC")) {
                    if (coursesUC.containsKey(course)) {
                        coursesUC.get(course).add(key);
                    }
                }
            }
            else if (arg.get(key).get("PE") != null) {
                for (String course : arg.get(key).get("PE")) {
                    if (coursesPE.containsKey(course)) {
                        coursesPE.get(course).add(key);
                    }
                }
            }
            else if (arg.get(key).get("UE") != null) {
                for (String course : arg.get(key).get("UE")) {
                    if (coursesUE.containsKey(course)) {
                        coursesUE.get(course).add(key);
                    }
                }
            }
        }

        for (String key: coursesPC.keySet()){
            Collections.sort(coursesPC.get(key));
        }
        for (String key: coursesUC.keySet()){
            Collections.sort(coursesUC.get(key));
        }
        for (String key: coursesPE.keySet()){
            Collections.sort(coursesPE.get(key));
        }
        for (String key: coursesUE.keySet()){
            Collections.sort(coursesUE.get(key));
        }

        return true;
    }
 
    private boolean setEmployeeData(HashMap<String, HashMap<String,String>> arg){
        for (String key : arg.keySet()) {
            employees.put(key,arg.get(key));
        }
        // generate random employee id and set them as id for each employee
        return true;
    }
 
 
    private boolean setExtraAdministrativeRole(ArrayList<String>arg){
        for (String key : arg) {
            if (employees.containsKey(key)){
                employeesWithExtraAdministrativeRole.add(key);
            }
            else{
//                System.out.println("Invalid Employee ID");
                // System.exit(0);
                // return false;
            }
        }
        return true;
    }
    
   private boolean checkFull(String desg, int Past, int Pref, boolean extraAdministrativeRole, String current, int latter){
        if (extraAdministrativeRole && (Past!=0 || Pref!=0)) return true;
        if (desg.equals("SP") || desg.equals("HOD")){
            if (Past==1 && current.equals("past")) return true;
            else if (Pref==1 && current.equals("pref")) return true;
            else if (latter==1 && current.equals("latter")) return true;
        }
        else if (desg.equals("ASCP") || desg.equals("ASP")){
            if (Past==1 && current.equals("past")) return true;
            else if (Pref==2 && current.equals("pref")) return true;
        }
        else if (desg.equals("New")){
            if (Past==1 && current.equals("past")) return true;
            else if (Pref==2 && current.equals("pref")) return true;
            else if (latter==2 && current.equals("latter")) return true;
        }
        return false;
    }

       private boolean checkFull(String desg, int Past, int Pref, boolean extraAdministrativeRole, String current){
        int latter=0;
        if (extraAdministrativeRole && (Past!=0 || Pref!=0)) return true;
        if (desg.equals("SP") || desg.equals("HOD")){
            if (Past==1 && current.equals("past")) return true;
            else if (Pref==1 && current.equals("pref")) return true;
            else if (latter==1 && current.equals("latter")) return true;
        }
        else if (desg.equals("ASCP") || desg.equals("ASP")){
            if (Past==1 && current.equals("past")) return true;
            else if (Pref==2 && current.equals("pref")) return true;
        }
        else if (desg.equals("New")){
            if (Past==1 && current.equals("past")) return true;
            else if (Pref==2 && current.equals("pref")) return true;
            else if (latter==2 && current.equals("latter")) return true;
        }
        return false;
    }
 
    private boolean  generateTimetable(){
        HashMap<String, Integer[][]> emplyeeUsedSlots = new HashMap<>();
        for (String key : employees.keySet()) {
            Integer[][] usedSlots = new Integer[7][24];
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 24; j++) {
                    usedSlots[i][j] = 0;
                }
            }
            emplyeeUsedSlots.put(key, usedSlots);
        }

        HashMap<String,HashMap<String,String>> employeeAssigned = new HashMap<>();
        ArrayList<String> notAllotedCoursePermissibleAllotees=new ArrayList<>();
        for (String key : employees.keySet()) {
            employeeAssigned.put(key,new HashMap<>());
            employeeAssigned.get(key).put("Desg",employees.get(key).get("Desg"));
            employeeAssigned.get(key).put("AllotedPref","0");
            employeeAssigned.get(key).put("AllotedPast","0");
            employeeAssigned.get(key).put("AllotedLatter","0");
            if (employeesWithExtraAdministrativeRole.contains(key)){
                employeeAssigned.get(key).put("Extra","1");
            }
            else{
                employeeAssigned.get(key).put("Extra","0");
                if (employees.get(key).get("Desg").equals("SP") || employees.get(key).get("Desg").equals("HOD")){
                    notAllotedCoursePermissibleAllotees.add(key);
                }
            }
        }
        Collections.sort(notAllotedCoursePermissibleAllotees);

        ArrayList<String> allotedCoursesPC = new ArrayList<>();
        ArrayList<String> unallotedCoursesPC = new ArrayList<>();
        ArrayList<String> allotedCoursesUC = new ArrayList<>();
        ArrayList<String> unallotedCoursesUC = new ArrayList<>();
        ArrayList<String> allotedCoursesPE = new ArrayList<>();
        ArrayList<String> unallotedCoursesPE = new ArrayList<>();
        ArrayList<String> allotedCoursesUE = new ArrayList<>();
        ArrayList<String> unallotedCoursesUE = new ArrayList<>();
        for (int d=0;d<7;++d){
            for (int t=0;t<24;++t){
                for (String coursePC: coursesPC.keySet()){
                        boolean allotedL=false;
                    if (coursesPC.get(coursePC).size()>0){
                        String employee = coursesPC.get(coursePC).get(0);
                        if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"pref")){
                            timetable.get(d).get(t).put(coursePC,employee);
                            allotedCoursesPC.add(coursePC);
                            emplyeeUsedSlots.get(employee)[d][t]=1;
                            allotedL=true;
                            employeeAssigned.get(employee).put("AllotedPref",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref"))+1));
                        }
                    }
                    if (this.prevCourseTakenData.containsKey(coursePC) && !allotedL){
                        for (String employee: this.prevCourseTakenData.get(coursePC)){
                                if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"past")){
                                    timetable.get(d).get(t).put(coursePC,employee);
                                    allotedCoursesPC.add(coursePC);
                                    emplyeeUsedSlots.get(employee)[d][t]=1;
                                    employeeAssigned.get(employee).put("AllotedPast",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast"))+1));
                                    allotedL=true;
                                    break;
                                }
                        }
                        }
                    if (!allotedL && !unallotedCoursesPC.contains(coursePC)){
                        unallotedCoursesPC.add(coursePC);
                    }
                    else if (allotedL && unallotedCoursesPC.contains(coursePC)){
                        unallotedCoursesPC.remove(coursePC);
                    }
                }
                for (String allotedCoursePC : allotedCoursesPC){
                    coursesPC.remove(allotedCoursePC);
                }

                for (String courseUC: coursesUC.keySet()){
                        boolean allotedL=false;
                    if (coursesUC.get(courseUC).size()>0){
                        String employee = coursesUC.get(courseUC).get(0);
                        if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"pref")){
                            timetable.get(d).get(t).put(courseUC,employee);
                            allotedCoursesUC.add(courseUC);
                            emplyeeUsedSlots.get(employee)[d][t]=1;
                            allotedL=true;
                            employeeAssigned.get(employee).put("AllotedPref",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref"))+1));
                        }
                    }
                    if (this.prevCourseTakenData.containsKey(courseUC) && !allotedL){
                        for (String employee: this.prevCourseTakenData.get(courseUC)){
                                if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"past")){
                                    timetable.get(d).get(t).put(courseUC,employee);
                                    allotedCoursesUC.add(courseUC);
                                    emplyeeUsedSlots.get(employee)[d][t]=1;
                                    employeeAssigned.get(employee).put("AllotedPast",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast"))+1));
                                    allotedL=true;
                                    break;
                                }
                        }
                   
                        }
                    if (!allotedL && !unallotedCoursesUC.contains(courseUC)){
                        unallotedCoursesUC.add(courseUC);
                    }
                    else if (allotedL && unallotedCoursesUC.contains(courseUC)){
                        unallotedCoursesUC.remove(courseUC);
                    }
                }
                for (String allotedCourseUC : allotedCoursesUC){
                    coursesUC.remove(allotedCourseUC);
                }

                for (String course: coursesPE.keySet()){
                        boolean allotedL=false;
                    if (coursesPE.get(course).size()>0){
                        String employee = coursesPE.get(course).get(0);
                        if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"pref")){
                            timetable.get(d).get(t).put(course,employee);
                            allotedCoursesPE.add(course);
                            emplyeeUsedSlots.get(employee)[d][t]=1;
                            allotedL=true;
                            employeeAssigned.get(employee).put("AllotedPref",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref"))+1));
                        }
                    }
                    if (this.prevCourseTakenData.containsKey(course) && !allotedL){
                        for (String employee: this.prevCourseTakenData.get(course)){
                                if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"past")){
                                    timetable.get(d).get(t).put(course,employee);
                                    allotedCoursesPE.add(course);
                                    emplyeeUsedSlots.get(employee)[d][t]=1;
                                    employeeAssigned.get(employee).put("AllotedPast",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast"))+1));
                                    allotedL=true;
                                    break;
                                }
                        }
                
                        }
                    if (!allotedL && !unallotedCoursesPE.contains(course)){
                        unallotedCoursesPE.add(course);
                    }
                    else if (allotedL && unallotedCoursesPE.contains(course)){
                        unallotedCoursesPE.remove(course);
                    }
                }
                for (String allotedCoursePE : allotedCoursesPE){
                    coursesPE.remove(allotedCoursePE);
                }

            for (String course: coursesUE.keySet()){
                        boolean allotedL=false;
                    if (coursesUE.get(course).size()>0){
                        String employee = coursesUE.get(course).get(0);
                        if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"pref")){
                            timetable.get(d).get(t).put(course,employee);
                            allotedCoursesUE.add(course);
                            emplyeeUsedSlots.get(employee)[d][t]=1;
                            employeeAssigned.get(employee).put("AllotedPref",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref"))+1));
                        }
                    }
                     if (this.prevCourseTakenData.containsKey(course) && !allotedL){
                        for (String employee: this.prevCourseTakenData.get(course)){
                                if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"past")){
                                    timetable.get(d).get(t).put(course,employee);
                                    allotedCoursesUE.add(course);
                                    emplyeeUsedSlots.get(employee)[d][t]=1;
                                    employeeAssigned.get(employee).put("AllotedPast",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast"))+1));
                                    allotedL=true;
                                    break;
                                }
                        }
                  
                        }
                    if (!allotedL && !unallotedCoursesUE.contains(course)){
                        unallotedCoursesUE.add(course);
                    }
                    else if (allotedL && unallotedCoursesUE.contains(course)){
                        unallotedCoursesUE.remove(course);
                    }
                    // coursesUE.remove(course);
                }
                for (String allotedCourseUE : allotedCoursesUE){
                    coursesUE.remove(allotedCourseUE);
                }
            }
        }

        for (int d=0;d<7;++d){
            for (int t=0;t<24;++t){
                for (String coursePC: coursesPC.keySet()){
                        boolean allotedL=false;
                    if (coursesPC.get(coursePC).size()>0 ){
                        String employee = coursesPC.get(coursePC).get(0);
                          if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                            timetable.get(d).get(t).put(coursePC,employee);
                            allotedCoursesPC.add(coursePC);
                            emplyeeUsedSlots.get(employee)[d][t]=1;
                            allotedL=true;
                            employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                            employeeAssigned.get(employee).put("AllotedPref",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref"))+1));
                        }
                    }
                    if (this.prevCourseTakenData.containsKey(coursePC) && !allotedL){
                        for (String employee: this.prevCourseTakenData.get(coursePC)){
                                if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                                    timetable.get(d).get(t).put(coursePC,employee);
                                    allotedCoursesPC.add(coursePC);
                                    emplyeeUsedSlots.get(employee)[d][t]=1;
                                    employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                                    employeeAssigned.get(employee).put("AllotedPast",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast"))+1));
                                    allotedL=true;
                                    break;
                                }
                        }
                        }
                    if (!allotedL){
                        for (String employee: notAllotedCoursePermissibleAllotees){
                            System.out.println("Employee: "+employee+" "+coursePC);
                            if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                                timetable.get(d).get(t).put(coursePC,employee);
                                allotedCoursesPC.add(coursePC);
                                emplyeeUsedSlots.get(employee)[d][t]=1;
                                allotedL=true;
                                employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                                break;
                            }
                        }
                        if (!allotedL  && !unallotedCoursesPC.contains(coursePC)) unallotedCoursesPC.add(coursePC);
                        else if (allotedL && unallotedCoursesPC.contains(coursePC)) unallotedCoursesPC.remove(coursePC);
                    }
                    else if (allotedL && unallotedCoursesPC.contains(coursePC)){
                        unallotedCoursesPC.remove(coursePC);
                    }

                    }
                
                for (String allotedCoursePC : allotedCoursesPC){
                    coursesPC.remove(allotedCoursePC);
                }

                for (String courseUC: coursesUC.keySet()){
                        boolean allotedL=false;
                    if (coursesUC.get(courseUC).size()>0){
                        String employee = coursesUC.get(courseUC).get(0);
                        if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                            timetable.get(d).get(t).put(courseUC,employee);
                            allotedCoursesUC.add(courseUC);
                            emplyeeUsedSlots.get(employee)[d][t]=1;
                            allotedL=true;
                            employeeAssigned.get(employee).put("AllotedPref",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref"))+1));
                            employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                        }
                    }
                    if (this.prevCourseTakenData.containsKey(courseUC) && !allotedL){
                        for (String employee: this.prevCourseTakenData.get(courseUC)){
                                if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                                    timetable.get(d).get(t).put(courseUC,employee);
                                    allotedCoursesUC.add(courseUC);
                                    emplyeeUsedSlots.get(employee)[d][t]=1;
                                    employeeAssigned.get(employee).put("AllotedPast",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast"))+1));
                                    employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                                    allotedL=true;
                                    break;
                                }
                        }
                   
                        }
                    if (!allotedL){
                        for (String employee: notAllotedCoursePermissibleAllotees){
                            if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                                timetable.get(d).get(t).put(courseUC,employee);
                                allotedCoursesUC.add(courseUC);
                                emplyeeUsedSlots.get(employee)[d][t]=1;
                                allotedL=true;
                                employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                                break;
                            }
                        }
                        if (!allotedL  && !unallotedCoursesUC.contains(courseUC)) unallotedCoursesUC.add(courseUC);
                        else if (allotedL && unallotedCoursesUC.contains(courseUC)){
                            unallotedCoursesUC.remove(courseUC);
                        }
                    }
                    else if (allotedL && unallotedCoursesUC.contains(courseUC)){
                        unallotedCoursesUC.remove(courseUC);
                    }

                    }
                for (String allotedCourseUC : allotedCoursesUC){
                    coursesUC.remove(allotedCourseUC);
                }

                for (String course: coursesPE.keySet()){
                        boolean allotedL=false;
                    if (coursesPE.get(course).size()>0){
                        String employee = coursesPE.get(course).get(0);
                        if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                            timetable.get(d).get(t).put(course,employee);
                            allotedCoursesPE.add(course);
                            emplyeeUsedSlots.get(employee)[d][t]=1;
                            allotedL=true;employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                            employeeAssigned.get(employee).put("AllotedPref",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref"))+1));
                        }
                    }
                    if (this.prevCourseTakenData.containsKey(course) && !allotedL){
                        for (String employee: this.prevCourseTakenData.get(course)){
                                if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                                    timetable.get(d).get(t).put(course,employee);
                                    allotedCoursesPE.add(course);
                                    emplyeeUsedSlots.get(employee)[d][t]=1;
                                    employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                                    employeeAssigned.get(employee).put("AllotedPast",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast"))+1));
                                    allotedL=true;
                                    break;
                                }
                        }
                
                        }
                    if (!allotedL){
                        for (String employee: notAllotedCoursePermissibleAllotees){
                            if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                                timetable.get(d).get(t).put(course,employee);
                                allotedCoursesPE.add(course);
                                emplyeeUsedSlots.get(employee)[d][t]=1;
                                allotedL=true;
                                employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                                break;
                            }
                        }
                        if (!allotedL  && !unallotedCoursesPE.contains(course)) unallotedCoursesPE.add(course);
                        else if (allotedL && unallotedCoursesPE.contains(course)){
                            unallotedCoursesPE.remove(course);
                        }
                    }
                    else if (allotedL && unallotedCoursesPE.contains(course)){
                        unallotedCoursesPE.remove(course);
                    }

                    }
                for (String allotedCoursePE : allotedCoursesPE){
                    coursesPE.remove(allotedCoursePE);
                }
                for (String course: coursesUE.keySet()){
                        boolean allotedL=false;
                    if (coursesUE.get(course).size()>0){
                        String employee = coursesUE.get(course).get(0);
                        if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                            timetable.get(d).get(t).put(course,employee);
                            allotedCoursesUE.add(course);
                            emplyeeUsedSlots.get(employee)[d][t]=1;
                            employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                            employeeAssigned.get(employee).put("AllotedPref",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref"))+1));
                        }
                    }
                     if (this.prevCourseTakenData.containsKey(course) && !allotedL){
                        for (String employee: this.prevCourseTakenData.get(course)){
                                if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                                    timetable.get(d).get(t).put(course,employee);
                                    allotedCoursesUE.add(course);
                                    emplyeeUsedSlots.get(employee)[d][t]=1;
                                    employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                                    employeeAssigned.get(employee).put("AllotedPast",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast"))+1));
                                    allotedL=true;
                                    break;
                                }
                        }
                  
                        }
                    if (!allotedL){
                        for (String employee: notAllotedCoursePermissibleAllotees){
                            if (emplyeeUsedSlots.get(employee)[d][t]==0 && !checkFull(employeeAssigned.get(employee).get("Desg"),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPast")),Integer.parseInt(employeeAssigned.get(employee).get("AllotedPref")),employeeAssigned.get(employee).get("Extra").equals("1"),"latter",Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter")))){
                                timetable.get(d).get(t).put(course,employee);
                                allotedCoursesUE.add(course);
                                emplyeeUsedSlots.get(employee)[d][t]=1;
                                allotedL=true;
                                employeeAssigned.get(employee).put("AllotedLatter",String.valueOf(Integer.parseInt(employeeAssigned.get(employee).get("AllotedLatter"))+1));
                                break;
                            }
                        }
                        if (!allotedL && !unallotedCoursesUE.contains(course)) unallotedCoursesUE.add(course);
                        else if (allotedL && unallotedCoursesUE.contains(course)){
                            unallotedCoursesUE.remove(course);
                        }
                    }
                    else if (allotedL && unallotedCoursesUE.contains(course)){
                        unallotedCoursesUE.remove(course);
                    }
                }
                for (String allotedCourseUE : allotedCoursesUE){
                    coursesUE.remove(allotedCourseUE);
                }
        }
        }
        System.out.println("Prev Course Taken Data: "+this.prevCourseTakenData);
        System.out.println("employee assigned: "+employeeAssigned);
        System.out.println("Allowed for latter allotemenet: "+notAllotedCoursePermissibleAllotees);
        System.out.println("Alloted Courses PC: "+allotedCoursesPC);
        System.out.println("Unalloted Courses PC: "+unallotedCoursesPC);
        System.out.println("Alloted Courses UC: "+allotedCoursesUC);
        System.out.println("Unalloted Courses UC: "+unallotedCoursesUC);
        System.out.println("Alloted Courses PE: "+allotedCoursesPE);
        System.out.println("Unalloted Courses PE: "+unallotedCoursesPE);
        System.out.println("Alloted Courses UE: "+allotedCoursesUE);
        System.out.println("Unalloted Courses UE: "+unallotedCoursesUE);
        System.out.println();
        return true;
    }
}

public class ttGen{
    public static void main(String args[]){
                try{
                    
             Class.forName("com.mysql.cj.jdbc.Driver");
           java.sql.Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tejes?useSSL=false","root","wartejes28") ;
            String sqli="select * from courses;";
            PreparedStatement stm=conn.prepareStatement(sqli);
            ResultSet rs=stm.executeQuery();   
             HashMap<String,String> courses = new HashMap<>();
            while (rs.next()) courses.put(rs.getString("course_id"),rs.getString("course_type"));
            
            String sqlw="select * from wishlist;";
            PreparedStatement stmw=conn.prepareStatement(sqlw);
            ResultSet rsw=stmw.executeQuery();   
             ArrayList<String> wishlist = new ArrayList<>();
            while (rsw.next()) wishlist.add(rsw.getString("course_id"));
            
            String sqliW="select * from employees;";
            PreparedStatement stmW=conn.prepareStatement(sqliW);
            ResultSet rsW=stmW.executeQuery();   
                     HashMap<String,HashMap<String,String>> employeeData = new HashMap<>();
                ArrayList<String> employeesWithExtraAdministrativeRole = new ArrayList<>();
            while (rsW.next()){
                String b = rsW.getString("extraAdmin");
                if (b!=null && b.equals(new String("yes"))){ 
                    employeesWithExtraAdministrativeRole.add(rsW.getString("extraAdmin"));
                }
                employeeData.put(rsW.getString("employee_id"),(new HashMap<String,String>()));
                        employeeData.get(rsW.getString("employee_id")).put("name",rsW.getString("employee_name"));
                        employeeData.get(rsW.getString("employee_id")).put("Desg",rsW.getString("employee_des"));
            }
            
            String sqliP="select * from prevCourses;";
            PreparedStatement stmP=conn.prepareStatement(sqliP);
            ResultSet rsP=stmP.executeQuery();   
             HashMap<String,ArrayList<String>> prevCourseTakenData = new HashMap<>();
            while (rsP.next()){
                if (prevCourseTakenData.containsKey(rsP.getString("employee_id"))){
                prevCourseTakenData.get(rsP.getString("employee_id")).add(rsP.getString("course_id"));
                }
                else{
                    prevCourseTakenData.put(rsP.getString("employee_id"), new ArrayList<String>());
                    prevCourseTakenData.get(rsP.getString("employee_id")).add(rsP.getString("course_id"));
                }
                }
            
            String sqliPr="select * from facultyPref;";
            PreparedStatement stmPr=conn.prepareStatement(sqliPr);
            ResultSet rsPr=stmPr.executeQuery();  
             HashMap<String,HashMap<String,ArrayList<String>>> prefCourse = new HashMap<>();
            while (rsPr.next()){
                if (prefCourse.containsKey(rsPr.getString("employee_id"))){
                prefCourse.get(rsPr.getString("employee_id")).get(rsPr.getString("course_type")).add(rsPr.getString("course_id"));
                    
                }
                else{
                    prefCourse.put(rsPr.getString("employee_id"), new HashMap<>());
                    prefCourse.get(rsPr.getString("employee_id")).put("UE",new ArrayList<>());
                    prefCourse.get(rsPr.getString("employee_id")).put("PE",new ArrayList<>());
                    prefCourse.get(rsPr.getString("employee_id")).put("UC",new ArrayList<>());
                    prefCourse.get(rsPr.getString("employee_id")).put("PC",new ArrayList<>());
                    prefCourse.get(rsPr.getString("employee_id")).get(rsPr.getString("course_type")).add(rsPr.getString("course_id"));
                }
                }
            
            ttgenerator tt = new ttgenerator(
                    wishlist,
                    employeeData,
                    employeesWithExtraAdministrativeRole,
                    courses,
                    prevCourseTakenData,
                    prefCourse
            );
            System.out.println(tt.getTimetable());
        }catch(Exception e){
            System.out.println(e);
        }
    }
}