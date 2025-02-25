
# ParserEjadaJackson

# XML to JSON Parser Using Jackson
This project demonstrates how to parse an XML file, convert it to JSON using Jackson, and display the node names for user interaction. The user can select a node, and the program will show its corresponding value.

# Features!
-Reads XML from a file.
- Convert XML to Json
- Extracts specific key-value pairs from the JSON data.
- Stores the data in a map for easy retrieval.
- Skips certain fields (e.g., the array or the NON-objects) based on specific conditions.
- Handles nested JSON structures and arrays.
- interact with the end user to retrive the selected node 
- Maven 
- CI ready (run maven commands)

# Prerequisites
Java 8 or higher
Maven for dependency management
IntelliJ IDEA (or any Java IDE)

# Setup Instructions
1. Clone the Repository
   Project is hosted on GitHub, clone it using the following command:
```sh
$ git clone <repository_url>
```

2- Build the Project from the directory which contain the cloned project

```sh
$ mvn clean install
```
3-Install the dependencies using this command

```sh
$ mvn dependency:sources
```
4- Run 

```sh
mvn exec:java -Dexec.mainClass="org.example.JacksonXmlToJsonParser"
```
