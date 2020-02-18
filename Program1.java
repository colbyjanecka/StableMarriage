/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.util.*;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the Stable Marriage problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    @Override
    public boolean isStableMatching(Matching marriage) {

        int employeeCount = marriage.getEmployeeCount();
        int locationCount = marriage.getLocationCount();

        ArrayList<ArrayList<Integer>> locationPreference = marriage.getLocationPreference();
        ArrayList<ArrayList<Integer>> employeePreference = marriage.getEmployeePreference();
        ArrayList<Integer> openSlots = marriage.getLocationSlots();

        ArrayList<Integer> employeeMatching = marriage.getEmployeeMatching();
        ArrayList<ArrayList<Integer>> data = new ArrayList<>();


        for(int i = 0; i < locationCount; i++){
            data.add(new ArrayList<Integer>());
        }

        for(int employee = 0; employee < employeeMatching.size(); employee++){
            int location = employeeMatching.get(employee);
            if(location != -1) data.get(location).add(employee);
        }

        for(int otherEmployee = 0; otherEmployee < employeeCount; otherEmployee++) {

            if (employeeMatching.get(otherEmployee) == -1) {

                for (int thisEmployee = 0; thisEmployee < employeeCount; thisEmployee++) {

                    if (employeeMatching.get(thisEmployee) != -1) {

                        ArrayList<Integer> thisLocationPreference = locationPreference.get(employeeMatching.get(thisEmployee));
                        System.out.println(locationPreference.get(employeeMatching.get(thisEmployee)));

                        for (int i = 0; thisLocationPreference.get(i) != thisEmployee; i++) {

                            if (thisLocationPreference.get(i) == otherEmployee) {
                                return false;
                            }

                        }

                    }

                }

            }
        }
        for(int otherEmployee = 0; otherEmployee < employeeCount; otherEmployee++) {

            int otherEmployeeLocation = employeeMatching.get(otherEmployee);

            for(int thisEmployee = 0; thisEmployee < employeeCount; thisEmployee++) {

                int thisEmployeeLocation = employeeMatching.get(thisEmployee);

                if(otherEmployeeLocation != -1 && thisEmployeeLocation != -1) {
                    ArrayList<Integer> otherEmployeeLocationPreferences = locationPreference.get(otherEmployeeLocation);

                    ArrayList<Integer> thisEmployeeLocationPreferences = locationPreference.get(thisEmployeeLocation);

                    if (otherEmployeeLocationPreferences.indexOf(thisEmployee) < otherEmployeeLocationPreferences.indexOf(otherEmployee)) {
                        if (thisEmployeeLocationPreferences.indexOf(otherEmployee) < thisEmployeeLocationPreferences.indexOf(thisEmployee)) {
                            return false;
                        }
                    }

                }
            }

        }


        return true;
    }



    /**
     * Determines a employee optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_employeeoptimal(Matching marriage) {


        ArrayList<ArrayList<Integer>> currentEmployeePreferences = new ArrayList<>();
        ArrayList<ArrayList<Integer>> currentLocationPreferences = new ArrayList<>();

        ArrayList<Integer> openPositions = marriage.getLocationSlots();
        int openPositionsCount = 0;
        List<Integer> openLocations = new LinkedList<>();
        for(int i = 0; i < openPositions.size(); i++){
            if(openPositions.get(i) > 0 ) {
                openLocations.add(i);
                openPositionsCount += openPositions.get(i);
                currentLocationPreferences.add(marriage.getLocationPreference().get(i));
            }
        }

        ArrayList<Integer> freeEmployees = new ArrayList<>(marriage.getEmployeeCount());
        ArrayList<Integer> matchings = new ArrayList<>();
        for(int i = 0; i < marriage.getEmployeeCount(); i++){
            freeEmployees.add(i);
            matchings.add(-1);
            currentEmployeePreferences.add(marriage.getEmployeePreference().get(i));
        }

        printInfo(marriage);
        System.out.println("OPEN POSITIONS COUNT: " + openPositionsCount);


        boolean run = true;
        int counter = 0;

        //System.out.println("openLocations: " + openLocations.toString());

        // while there is a free employee
        while(!freeEmployees.isEmpty() && run){

            int thisEmployee = freeEmployees.remove(0);
            //System.out.println(counter + "-------------------------------------------------");
            //System.out.println("NOW WORKING WITH EMPLOYEE " + thisEmployee + " ~ " + currentEmployeePreferences.get(thisEmployee));
            ArrayList<Integer> thisEmployeesPreferences = currentEmployeePreferences.get(thisEmployee);
            boolean foundBetterOption = false;
            for(int possibleLocation : thisEmployeesPreferences) {
            //while(!thisEmployeesPreferences.isEmpty()){

                if(foundBetterOption){
                    break;
                }

                if(openPositions.get(possibleLocation)>0) {
                    matchings.set(thisEmployee, possibleLocation);
                    foundBetterOption = true;
                    openPositions.set(possibleLocation, openPositions.get(possibleLocation)-1);
                    break;

                } else {    // possibleLocation is already employing otherEmployee

                    for(int i = 0; i < matchings.size(); i++){

                        if(matchings.get(i) == possibleLocation) {

                            int otherEmployee = i;
                            ArrayList<Integer> possibleLocationPreferences = currentLocationPreferences.get(possibleLocation);


                            if (possibleLocationPreferences.indexOf(otherEmployee) < possibleLocationPreferences.indexOf(thisEmployee)) {

                            } else {

                                matchings.set(thisEmployee, possibleLocation);
                                matchings.set(otherEmployee, -1);
                                currentEmployeePreferences.get(otherEmployee).add(possibleLocation);
                                freeEmployees.add(otherEmployee);
                                foundBetterOption = true;
                                i = matchings.size();

                                break;
                            }

                        }
                    }
                }

            }
        }

        System.out.println("matches: " + matchings);

        marriage.setEmployeeMatching(matchings);
        return marriage;
    }

    /**
     * Determines a location optimal solution to the Stable Marriage problem from the given input set.
     * Study the description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_locationoptimal(Matching marriage) {

        ArrayList<Integer> openPositions = marriage.getLocationSlots();
        List<Integer> openLocations = new LinkedList<>();
        for(int i = 0; i < openPositions.size(); i++){
            if(openPositions.get(i) > 0 ) {
                openLocations.add(i);
            }
        }

        ArrayList<Integer> matchings = new ArrayList<>();
        for(int i = 0; i < marriage.getEmployeeCount(); i++){
            matchings.add(-1);
        }

        System.out.println("openLocations: " + openLocations.toString());
        System.out.println("matchings: " + matchings.toString());

        for (int i = 0; i < marriage.getLocationCount(); i++) {

            //int thisLocation = openLocations.remove(0);

            int thisLocation = i;

            ArrayList<Integer> thisLocationsPreferences = marriage.getLocationPreference().get(thisLocation);

            System.out.println("Now working with location " + thisLocation + " ~ " + thisLocationsPreferences);

            for(int j=0; j < marriage.getEmployeeCount() && openPositions.get(i)!=0; j++){

                int possibleEmployee = thisLocationsPreferences.get(j);

                System.out.println("Trying employee " + possibleEmployee);
                System.out.println("This locations open postion count:  " + openPositions.get(thisLocation));

                if( matchings.get(possibleEmployee) == -1 && openPositions.get(thisLocation)!=0) {
                    System.out.println("Matched " + possibleEmployee + " - " + thisLocation);
                    matchings.set(possibleEmployee, thisLocation);
                    System.out.println("matchings: " + matchings.toString());
                    openPositions.set(thisLocation, openPositions.get(thisLocation)-1);
                    //break;
                } else {
                    int otherLocation = matchings.get(possibleEmployee);

                    ArrayList<Integer> possibleEmployeePreferences = marriage.getEmployeePreference().get(possibleEmployee);

                    System.out.println("Seeing if employee " + possibleEmployee + " likes " + thisLocation + " or " + otherLocation + " more.");
                    if(possibleEmployeePreferences.indexOf(otherLocation) > possibleEmployeePreferences.indexOf(thisLocation)){

                        System.out.println("He likes " + thisLocation + " more!");
                        matchings.set(possibleEmployee, thisLocation);
                        System.out.println("Matched " + possibleEmployee + " - " + thisLocation);
                        System.out.println("matchings: " + matchings.toString());
                        openPositions.set(thisLocation, openPositions.get(thisLocation)-1);
                        openPositions.set(otherLocation, openPositions.get(otherLocation)+1);
                        //break;
                    } else {
                        //openLocations.add(thisLocation);
                    }
                }
                System.out.println("Open Positions: " + openPositions);
            }
        }
        marriage.setEmployeeMatching(matchings);
        return marriage;
    }

    // REMOVE THIS --------------------------------------------------------------------------------------- !!!!
    private ArrayList<Integer> employeesAtH(Integer storeH, ArrayList<Integer> employee_matching, int n) {
        ArrayList<Integer> returnarr = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (employee_matching.get(i) == storeH) {
                returnarr.add(i);
            }
        }
        return returnarr;
    }

    private void printInfo(Matching marriage) {

        System.out.println(" TEST BLOCK ---------------------------------------");
        System.out.println("location_slots: " + marriage.getLocationSlots().toString());
        System.out.println("Employee preferences: " + marriage.getEmployeePreference().toString());
        System.out.println("location preferences: " + marriage.getLocationPreference().toString());
        System.out.println("# of locations: " + marriage.getLocationCount() + "; # of employees: " + marriage.getEmployeeCount());
        if(marriage.getEmployeeMatching() != null) System.out.println("matchings: " + marriage.getEmployeeMatching().toString());
        System.out.println("--------------------------------------------------");
    }

}
