<table>
  <tr>
    <td>

<img src="./app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png" width="128"/>
    </td>
    <td>

# FleetTracker
Android application for fleets to continue growing their businesses by keeping track of their inventory of vehicles.
    </td>
  </tr>
</table>

---

<table>
  <tr>
    <td>
      <img src="https://www.dropbox.com/scl/fi/0nl5q24uronb5c0f2mvbg/banner.png?rlkey=42hecs9sxyhsiuucphyj5j1i4&raw=1" width="300"/>
    </td>
    <td>

## Features
- Track and manage fleet vehicle inventory
- See the location of cars on Google Maps
- View an overview of current statistics (e.g., total crashes, cars under maintenance)
- Display trips completed by fleet vehicles
- Show the list of drivers
- Show the list of cars
- View detailed information about drivers and their driving style
- View detailed information about cars, including error codes (if any)
- English and Turkish language support in the application
  </td>
  </tr>
</table>


<img src="https://www.dropbox.com/scl/fi/j4ckw5r589s5aire2zcbp/app_screens.jpg?rlkey=3vu7agism7fagu466mvb24be5&raw=1" width="100%"/>

## Example Use Cases

- **Trip Analysis & Performance Tracking**  
   After a busy week, the manager reviews completed trips on the dashboard. By analyzing trip data and driver statistics, they identify which drivers completed the most trips safely and reward top performers, while scheduling additional training for those with risky driving styles.
- **Driver Performance Evaluation**  
  At the end of the month, the manager reviews each driver's trip history and driving style. By comparing statistics, they identify drivers with risky behaviors and arrange targeted training sessions, while recognizing top performers.
- **Maintenance Scheduling**  
  The fleet manager checks the overview of current statistics and notices several cars flagged for maintenance. Using the detailed car information and error codes, they schedule repairs and reassign trips to available vehicles, minimizing downtime.


## Project Structure

The diagram below illustrates the current class and package organization of the project.  
*Note: Class members are not shown to optimize space in the chart.*

![UML Diagram](https://raw.githubusercontent.com/gist/onurtas1993/3ded459ef85d000972f6218161dc90fc/raw/24ccd304b2fabb143456946690c96dc314740818/uml.svg)

## MVVM Architecture

This application is built using the MVVM architecture. The diagram below demonstrates the software design of the Dashboard module, which serves as a reference for the structure used throughout the rest

![UML Diagram 2](https://raw.githubusercontent.com/gist/onurtas1993/51ae383520e7d40938756e25b2481a97/raw/7c8bf85ae2df1d7d814c5daa9347a88be3c51d20/uml.svg)


## Notes

- This app is a prototype and does not use an actual web service or database. All data is simulated using remote `.json` files. The project showcases what can be achieved on the Android platform with multithreading application design and modern UX principles.

- To run the app locally, you need to create a Google Maps API key and add it to the `strings.xml` file.