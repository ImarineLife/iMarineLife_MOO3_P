# iMarineLife_MOO3_P
MarineLife_MOO3_P is a Project (_P) for the Anemone foundation (stichting Anemoon) - a national effort to gather information about the biodiversity under water in the Netherlands. The MOO3 specieslist is specifically meant for the coastal area. The app was created by the volunteer driven organisation iMarineLife, which provides this app and a more general library (iMarineLife_lib_P) which can be used for similar projects.

The library is very general and provides the framework of any app using it.
The library is (at this point) localized for dutch and english only.
The app is tailored to provide a specific
   - list of species (names, description, pictures)
   - list of locations
   - list of event specific information (profile parts)
   
Tailoring is achieved by altering some of the lists provided in the CurrentCatalog.java
    - ids
        - a list of integers that represent the species included in the fieldguide by their WORMS code (World Register of Marine Species). Other codes could be used, as long as they are integers and uniquely identify a species.
    - latinIds
      - a list of integers that represent the latin names for the species that are in the ids list as referenced by the string resources in latin.xml. The names of the string resources are directly derived from the integers in the ids list (latin999999). They also have to be entered into the array in the same order as the Ids.
    - commonIds
      - a hashmap linking the name 'common99999' and the integer as refereced by the string resource in common.xml
    - descrIds
      - a hasmap linking the name 'descr99999' and the integer as referenced by the string recourse in descr.xml
    - groupIds
      - a hashmap linking the latin group name to the integer referenced by the string resource in group.xml
    - allGroups
      - a string containing all lating group names available as specified in group.xml. Every name (also the first and last) has to have a $ before and a $ after ($pisces$$cnidaria$)
    - commonToGroups
      - a hashmap linking the generalized name for each species ('common99999') to the latin group name of the group it belongs to
    - locationNames
      - a hashmap linking a generalized name (which will also dictate the order in which locations are provided in the app) to the string resource in locations.xml
    - sightingChoices
      - a list of the possible sighting choices (generalized names p.e. C for common)
    - defaultChoice
      - a string containing one of the possible sighting choices that is to be used as the default (0 or ? is acceptable)
    - possibleDefaultChoices
      - a list of possible default choices (please only use 0 and ?). The user can specify if it wants to use a different default but it can only be one of the choices provided here.
    - checkboxChoices
      - a list of the possible checkbox values (generalized names p.e. egg, polyp)
    - checkValues
      - an array of strings, representing a comma separated list of checkboxes that will be provided with that species. This one is tricky - the position in the array specifies what species the values belong to (same order as 'ids'), null represents 'no checkbox' so if you want no checkboxes, you have to provide an array with the same amount of null values as the ids list contains ids.
    - valuesMap
      - a hashmap linking generalized names for every possible sightingchoice value and check value, to the integer referenced by the string resource in values.xml
    - profileParts (used on the second page of the log)
      - a set of arrays containing
        - the generalized name of the 'profile'part
        - whether it represents time spent in a specific fashion during this sighting event (diving, snorkling) and should therefore be part of a summation on the profile page. (true or false)
        - an order number that specifies the order in which te profile parts are shown on screen (the added ones should be first, the first value with a false ends the summed parts and splits the two types visually on screen
    - profilePartsMap
      - a hashmap that links the generalized name of the profilepart to the integer referenced by the string resource in profileparts.xml
    
Pictures can be provided in two forms
  - assets/smallpics contains png's with a specific name format (s99999.png where 99999 is the id of the species corresponding with the integer in ids) - these are obligatory.
  - alternative pictures can be provided in an obb file in a directory largepictures. The pngs have a slightly different name format) l999999.png (el not one, where 999999 is the id.....). These pictures will be shown when the screen is appropriately large or when the device is tilted.
    - when alternative pictures are provided, a salt (random byte[]) and appropriate base64PublicKey have to be provided in the CurrentCatalog. If not, these have to be null (as in this BOWL app)
