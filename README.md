# Fragment RetainInstanceTrue
Android - Use of AsyncTask with fragment retainInstance

Programming Mobile Services for Android Handheld Systems: Concurrency (Coursera.com)

## Assignment 3

Overview of the Downloaded Images Viewer Assignment

The purpose of this "mini-project" assignment is to develop an app that prompts the user for a list of URL to images and then downloads these images, filters the images, and displays them. You have a great degree of flexibility in how you design and implement this app, as long as it meets the following requirements

- Downloading and filtering the images should each be performed concurrently via different instances of the Android AsyncTask concurrency framework, which is covered here and here.
- All AsyncTasks should run concurrently using the AsyncTask.THREAD_POOL_EXECUTOR and the executeOnExecutor() method.
- The filter(s) used to process the image should at least be the gray-scale filter that's provided as part of the code skeletons distributed with the assignment. You're welcome to define other filters, but they are optional.
- The app must handle runtime configuration changes properly in the context of concurrently executing instances of AsyncTask. Information on how to handle these types of changes is available here and here.

Pattern and tutorial used:
http://www.androiddesignpatterns.com/2013/04/retaining-objects-across-config-changes.html
