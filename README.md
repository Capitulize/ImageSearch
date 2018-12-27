# ImageSearch
Project for Intro to Parallel Computing

Probably one of my favorite projects, I made this for a computer science course that involved threading and distributive systems. Essentially the project had us create a program that would accept an image, and scan through it to find particular sets of pixels that resembled waldo.

These sets of pixels were in a 2x2 format, and the image was typically very large, with thouands of pixels. The objective was to find these
sets of pixels quickly though, so I had to create separate threads to scan through the image quicker until it found all four characters. So instead of finding 1 character at a time, 
it would scan to try and find all four.
