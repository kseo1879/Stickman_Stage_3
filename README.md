### Coding Style
<a href="https://oracle.com/technetwork/java/codeconventions-150003.pdf">Oracle</a>

### Location of the Configuration file
src/main/resources/level_1.json

### Description of the Configuration file
The config file has the hero as its own JSONObject.
The immovable, movable and enemy entities are all stored in
three separate JSON arrays. Each entity that belongs to an
array requires attributes specific to that array. This facilitates
the creation process and allows you to add a new entity anywhere in
its corresponding JSON array.

## Acknowledgements
<a href="https://opengameart.org/content/top-down-2d-metal-box">Block png</a><br>

## Stage 3 Submission
There is three function that has been added. 
Each level has it's own target time try to finish them before the target time
Also each level you should aim to finish before 15 seconds!!
###Changing Through Different Level
There is total of three levels and this will be able to move around after the hero has reached a finish flag
###Score Keeper
The target time is located on each .json file locadted on src/main/resources.
There are total four levels containing information for each level. src/main/resources/level_1.json, 
src/main/resources/level_2.json, src/main/resources/level_3.json, src/main/resources/level_4.json
###Save and load
You can save using "S" (lower case) on keyboard and it will tell you which level is saved on the
screen. If you want to load the level you have saved you can press "L"(lower case) on keyboard
to load the level you have saved. You can load as many times as you want as long as you have a saved level.



