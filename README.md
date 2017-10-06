# ConnectU
IIT ConnectU Carpool app for IIT students and teachers!

# Description
This document 
[AndroidFinalProjppt.pdf](https://github.com/illinoistech-itm/connectu/blob/master/doc/AndroidFinalProjppt.pdf)
reviews the why for Carpool, shows actual ConnectU screenshots, 
considers issues, reviews the design, looks at future work needed.
 
## What can you do?

### Login

Google SignIn API is the sign-in method for this app simply because 
1) Valid and verified IIT students and teachers have `@hawk.iit.edu` or `@iit.edu` email address and 
2) IIT portal is required by both IIT and Google to actually sign in with those email address.  

If your IIT Google account was set up with Two-Factor authentication, that also works.  Once you successfully log in, the app knows who you are and automatically signs you in next time you start the app.

### ConnectU

ConnectU displays the Toolbar with your name and tabs at the top.  The person icon is the Register/Profile tab, the map icon is the Map tab and the car icon is the Ride/Carpool tab.  The option setting (three dots) at the top right corner - selects your state to be Driver, Rider or Inactive.  Note that the option icon will change accordingly. 

For the first time in the Register/Profile tab, you'll need to fill in your information and click the `Register New Account` button.  The data will be saved and the button will change to `Update Account`.  Subsequent visits will have this data prepopulated.

In the Map tab, there is the driver marker, rider (default) markers and the destination flag marker.  Tap or click any of these marker to see who or location of the user.  This is the set of markers that are in five mile radius of the driver.

In the Ride/Carpool tab, there is the Carpool title, date, time and list of drivers and riders.  Click on the date to bring up the date picker.  Click on the time to bring up the time picker.  Click on the name to say hello, on the chat icon to chat, on the phone to call.  Click on the car will simply say you are the driver!  To add rider(s) to this carpool, click on the person with plus - that rider will be added and the plus will disappear.  To remove the rider, click on the person icon and the plus will reappear when this rider is removed from the carpool.

### Logout

Back key to the SignIn view and click on the sign out button.

# Notes

## Build and Run with Android Studio

1) In Android Studio, select menu "Check out project from Version Control" and then select "GitHub"
2) Clone Repository dialop pops up - provide the Git Repository URL, the parent directory where you want the repository to clone to, the directory name and click the Clone button.
3) Checkout From Version Control dialog box pops up asking if you'd like to open the project - click yes.
4) Now you have everything you need and ready to rock and roll!  At this point I assume you know how to build and run. Go for it.  Code on!

## SHA1 Keys

Your SHA1 key needs to be provided, needs to be added this ConnectU certification configuration and finally added to `google-services.json` file. Once that's done you'll be able to build and successfully sign-in with Google SignIn API.

### Workaround until SHA1 key is added

In the AndroidManifest.xml file, there are two activities, `SignInActivity` and 
`TabLayoutActivity` and simply switch their names so that `TabLayoutActivity` would start first.

### Finding your SHA1 Key
Here are the steps to find your SHA1 key - see 
[this figure](https://github.com/illinoistech-itm/connectu/blob/master/doc/keystore.png) 
 for reference and do:

1) click/expand the Gradle tab on the right side of android studio.
2) expand the paths as shown in the figure
3) double click on the `signingReport` task.
4) MD5/SHA1 should show in the output window

In step 4, you may need to toggle the output buffer.
If needed, click the "task execution" or "text output" icon in the red circle shown in the figure.

Once you provided your SHA1 keys, I will add it to the certification store, regenerate the json file and push it to the repository.

By way of note, this is debug API key - 
If and when building ConnectU for production, a new Release SHA1 key will be needed. 
Just a few hoops to jump through.


# Authors

Michael McCartney <mmccartney@hawk.iit.edu>

Prashanth Boovaragavan <pboovaragavan@hawk.iit.edu>

Aditya Chebiyyam <achebiyyam@hawk.iit.edu>

Kapilan Kumanan <kkumanan@hawk.iit.edu>

# License and Copyright
[MIT](https://github.com/illinoistech-itm/connectu/blob/master/LICENSE)

Copyright (c) 2017 IIT ConnectU
