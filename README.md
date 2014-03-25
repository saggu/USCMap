USCMap
======

Java project - Spatial DB Queries

This is a Java project from my Database class at University of Southern California.

A map in the form of a map.jpg file is provided. 
3 txt files containing coordinates of students, buildings and announcementsystems are also provided.


To Initialize, we first need to populate the database with the data provided. Oracle libraries are used for this 
and Spatial component provided by Oracle is implemented.


To run the PopulateDB java file - 

java -classpath  ".\..\..\lib\classes111.jar;.\.." dbc.PopulateDB buildings.xy students.xy announcementSystems.xy


TO run the main program -

java -classpath ".\..\..\lib\classes111.jar;.\..\..\lib\sdoapi.jar;.\..\..\lib\sdoapi.zip;.\..\..\lib\sdoutl.jar;.\.." 
uscmap.SpatialProject

This project JGeometry library provided by Oracle to handle spatial queries, explanation at 

http://docs.oracle.com/cd/B28359_01/appdev.111/b28401/oracle/spatial/geometry/JGeometry.html


Feel free to checkout and suggest improvement.
