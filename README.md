# Minesweeper

Simple Minesweeper demo plugin for MegaMenus

Start a game with `/minesweeper [mines]` where mines is a number between 3 and 10.  
The command requires the permission `minesweeper.command`.

You can also start a game programatically using `new Minefield(mines).play(player);`  
Upon winning or losing the plugin emits Events for other plugins to react to (if such is desired).

![Alt text](screenshot-1.PNG?raw=true "In progress game")
