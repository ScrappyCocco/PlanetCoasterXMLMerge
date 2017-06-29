# Planet Coaster XML Merge
A little software for Planet Coaster translators that merge the old xml translation file with the new xml. (Leaving the translated sentences)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites (How to download and execute)

To execute this program you need Java, you can Download Java at https://www.java.com/download/

To run the code you can compile and execute with those 2 commands in cmd

```
javac *.java
java Window
```

Otherwise you can download (and execute) the jar file [Here](out/artifacts/PlanetCoasterXMLMerge_jar/)

### Using the program

The program is really easy to use: once you started it you have to choose:
* Your OLD translation file (for example 1.2.2)
* The NEW and **Original** translation file (for example 1.3).
You can download it from http://cdn.gulpeyrex.com/communitytranslations/sourcedata/VERSION/StringData.xml

Once you have chosen your files you can press "Process" and wait a couple of seconds until the final file (Called Final.xml) appear!
The program may write another file called "StringLoss.txt", this file contains the sentences in the old file that aren't present in the new file and that have been removed.

Please report any bug, i did what i could but the program may not be perfect.

## Authors

* **Franccc** - *Special Thanks to my friend Franccc for the initial code.*
* **ScrappyCocco** - *The rest of the work (as GUI and optimizations)*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
