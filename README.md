# VisualAid

Project made up of a python server side application and Android client application. The user can specify when they would like to capture an image with the stereo camera setup from the Android application.

####Equipment

Two web cams are set up to simulate a stereo camera. Android smart phine used for application control and user haptic feedback. Local machine used to host python server application and connect web cams.

##How to use

The various python application run scripts are defined in the `run_scripts.py` file. To run the final application with all feature use:
```
sh run_scripts.py run
```
The Android application can be connected by scanning a QR code for the local IP address of the machine on which the python application is being run. The web cams should also be connected to this machine.
