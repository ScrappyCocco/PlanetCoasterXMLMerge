![planet coaster icon](./src/PlanetCoasterXML/planet_icon.png)

![release info](releaseinfoimage.png)

# Planet Coaster XML Merge
A little software for Planet Coaster translators that merge the old xml translation file with the new xml. (Leaving the translated sentences).
You can use it too for checking duplicates or to print some values to a text file.

The program can easily be modified if you need to merge a completely different type of XML file.

## Getting Started

These instructions will get you a copy of the program for development and testing purposes, you can edit it as you want or just use it for Planet Coaster XML files.

### Prerequisites (How to edit or rebuild the software)

To execute this program you need Java, you can download Java [from here](https://www.java.com/download/).

If you want to re-compile the program, you need the JDK, you can download it [from here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

**All the code documentation of the program is available as Javadoc**, you can read it [here](https://scrappycocco.github.io/PlanetCoasterXMLMerge/).

To run the program you can re-compile it and execute it with those 2 commands in your terminal from src folder:

```
javac -cp "libs\guava-23.0.jar;libs\json-simple-1.1.1.jar;" PlanetCoasterXML\*.java
java -cp "libs\guava-23.0.jar;libs\json-simple-1.1.1.jar;." PlanetCoasterXML.Window
```

(Remember that on unix-like systems the `;` in the classpath become `:`, and path separator `\` become `/`)

If you are using Windows, you need "Java" in the PATH to use it from the command prompt, see it how [clicking here](https://goo.gl/iX4ZN7).

Otherwise you can download (and execute) the .jar or the .exe file [from here](out/artifacts/Final_Jar/).

### Download the program

You can download the last program release [here](https://github.com/ScrappyCocco/PlanetCoasterXMLMerge/releases).

### Using the program

The program is really easy to use: once you started it you have to choose:
* Your OLD translation file (for example 1.2.2)
* The NEW and **Original** translation file (for example 1.3.1).
You can download it from http://cdn.gulpeyrex.com/communitytranslations/sourcedata/VERSION/StringData.xml
(you have to change VERSION with the number, for example *.../1.3.1/StringData.xml* [(example link)](http://cdn.gulpeyrex.com/communitytranslations/sourcedata/1.6.2/StringData.xml))

Once you have chosen your files you can press "Process" and wait a couple of seconds until the final file (Called Final.xml) appear!
The program may write another file called "StringLoss.txt", this file contains the sentences in the old file that aren't present in the new file and that have been removed.

To check for duplicates just select a file and click the button, this will create a txt file if duplicates are found.

Please report any bug, the program may not be perfect.

## Built Using

* [Java Simple JSON](https://github.com/fangyidong/json-simple) - (To read information from a JSON file)
* [Google Guava](https://github.com/google/guava) - (For LinkedListMultimap)

## Authors

* **Franccc** - *Special Thanks to my friend Franccc for the initial code.*
* **ScrappyCocco** - *The rest of the work (GUI and code optimizations)*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
