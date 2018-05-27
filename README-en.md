# ChronoMap ![](https://github.com/HenriAugusto/ChronoMap/blob/master/Icons/ChronoMap%20Icon.png)

This the Main repository for the ChronoMap application.

[**Click here to go to the download page!**](https://github.com/HenriAugusto/ChronoMap/releases/tag/v0.1.0)

[**Clique aqui para ler em português**](https://github.com/HenriAugusto/ChronoMap/blob/master/README.md)


## What is it?

ChronoMap is an desktop software application to create, edit and view timelines.

## Why? 

Everything started as a personal project to aid me in my history studies (mainly art history). Then, as a teacher, i realized it was a very useful tool in our music classes because most students had basic understanding of the sequence of events but had not a good understanding of their _"position in time"_. For example: in western music history classes for example we spend a lot of time talking about Bach and later composers. The students don't get that the "Middle Ages" is actually a ~1000 year period. (So called Middle Ages by the Renaissance) and consequently can't compare this to the fact that the biggest part of the composers we study are in a period of less than 300 years. So i've decided to create this project in order to aid students to have a better understanding of the time scale of things.

It is important to notice that **the timeline is not a study object _per se_** but instead a **tool** for aiding students in their history studies. It helps then to understand _"positions in time"_ and allows then to group visually related events in order to study their _relative position_.

## Screenshot
![Looks like your browser can't display this image](https://github.com/HenriAugusto/ChronoMap/blob/v0.1.0/Readme%20Images/ChronoMap%20v0.1.0%20screenshot.png)

## Gif
![Looks like your browser can't display this image](https://github.com/HenriAugusto/ChronoMap/blob/v0.1.0/Readme%20Images/ChronoMap%20v0.1.0%20gif.gif)

## Features

* **Searchable events:** press Ctrl+F and find the event you're looking for and the app will center the view on it.
* **Integrated web-browser:** Save links to an event and view them inside the application. You can even specify the type of the link and, for example, if it's an audio it will play in the background.
* **Conditional view**: You can add named conditions (like "Composers", "Painters", etc) and select at any time which events you want to see. This is powered by conditional expressions syntax parsing so you can for example add an event for Beethoven and use the condition expression: __Composers && (ComposersClassical || ComposersRomantic)__
* **Embedded help:** press F1 at any time for a window which contains all the information you need. No need to open external PDF's.
* **XML Saves:** all the save data is stored in UTF-8 XML files that can be parsed by other apps for all kinds of statistics (plus it's human readable).
* **Art History Timeline:** It comes with the art history timeline that originated everything. It's not even close to be ready (if it will ever be) but is a good start ;)

## Future

- [ ] **3D Visualization:** 3D view of the timeline. __Easter egg!__ Actually you can peek an prototype pressing ctrl+3 after loading your timeline. Click and drag to move the view. F10 and F11 to zoom (use ctrl to change the size of the zoom steps)
