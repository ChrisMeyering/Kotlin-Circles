# Kotlin Circles
A simple application that genereates circles of random colors when the screen is clicked.
## Getting Started
Clone this repository and open it in Android Studio. Then, build the project and run it on desired device or emulator.

More information on how to get Android Studio up and running can be found [here](https://developer.android.com/training/basics/firstapp/running-app).

If you prefer to skip the IDE and run the app directly from the command line, follow [these steps](https://developer.android.com/studio/build/building-cmdline).

## Main Features
* SQLite Dtabase &mdash; used in this project to keep track of the set of circles created, as well as maintain the history of events to provide the undo/redo feature.
* Recyclerview &mdash; used in this project to display a list of circles (CircleListActivity.kt)
* ScaleGestureDetector &mdash; allows to modify the radius of the circles (DravView.kt)  