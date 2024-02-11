![CI](https://github.com/sienatime/JishoTomo/workflows/CI/badge.svg)

# Jisho Tomo

Jisho Tomo is an offline-first Japanese <=> English dictionary Android app.

![Screenshot_20240211-135225](https://github.com/sienatime/JishoTomo/assets/1372018/c6ad2f68-1460-444f-870a-a2aa5c1bfe1a)
![Screenshot_20240211-135232](https://github.com/sienatime/JishoTomo/assets/1372018/59819ffc-7f2e-4242-a334-099dd33e2c15)

The user is able to browse all of the entries in the dictionary, which are loaded one page at a time from the on-device database using Jetpack Paging. 

Search is implemented as full-text search (FTS) with Room ([read the blog post!](https://medium.com/@sienatime/enabling-sqlite-fts-in-room-2-1-75e17d0f0ff8)).

The detail view is written in Jetpack Compose.

![Screenshot_20240211-135355](https://github.com/sienatime/JishoTomo/assets/1372018/6a5dbc61-372b-43dd-924e-386d57512292)

The app's architecture is MVVM with coroutines. 

The app contains both unit tests and Espresso UI tests.

## Database

There is a companion [Rails app](https://github.com/sienatime/jmdict-rails) also on GitHub but it's a bit of a mess. It was designed to produce the .sqlite file
that is present in this repository. 

## Attributions

This app uses the [JMdict dictionary files](http://www.edrdg.org/jmdict/j_jmdict.html).

These files are the property of the [Electronic Dictionary Research and Development Group](http://www.edrdg.org/),
and are used in conformance with the Group's (license)[http://www.edrdg.org/edrdg/licence.html].

JLPT data is from [Jonathan Waller's site](http://www.tanos.co.uk/jlpt/), which is provided under a Creative Commons license.

Jisho Tomo was created as part of the Android Developer Nanodegree program by Udacity and Google.

## Usage

If you have any questions about the app, please get in touch!
