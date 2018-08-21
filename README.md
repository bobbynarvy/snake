# Snake

Snake game in Clojurescript

Play the game: [https://bobbynarvy.github.io/snake/](https://bobbynarvy.github.io/snake/)

## Overview

This is a fun little personal project written with the following learning objectives:

- To learn Clojurescript and its ecosystem through Figwheel
- To learn how to use core.async by applying it in a game

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2018 Robert Narvaez

Distributed under the Eclipse Public License either version 1.0 or any
later version.