# VisualAid

Project made up of a python server side application and Android client application. The user can specify when they would like to capture an image with the stereo camera setup from the Android application.

####Equipment

- Two web cams are set up to simulate a stereo camera.
- Android smart phine used for application control and user haptic feedback.
- Local machine used to host python server application and connect web cams.

##How to use

The various python application run scripts are defined in the `run_scripts.py` file. To run the final application with all feature use:
```
sh run_scripts.py run
```
The Android application can be connected by scanning a QR code for the local IP address of the machine on which the python application is being run. The web cams should also be connected to this machine.

##Output

The application will grab the current frame from the web cams. It will then find the disparity map for these images using the settings in the `settings.json` file (these can be edited by running the `sh run_scripts.sh tuner` script). The disparity image is then used for two things:
- The disparity image is maniplated using the `morph_ops()` function to erode, dilate, open and close the specular noise in the image and get a more accurate result. The manipulated image is then analysed to find the areas in the scene that are closest to the cameras. These areas are returned to the user over WiFi in the Android application.
- The disparity image is also used to build a 3D point cloud map of the depth in the scene. To do this the matrix from `input/disp_to_depth_mat.npy` is loaded. This point cloud file is exported to the application directory as `pointCloud.ply` and can be opened using [meshlab](http://meshlab.sourceforge.net/).

The captured frames are then run against the Google Cloud Vision API to find objects in the scene. For the example image, this request returned a result of `Found label: wood burning stove`.

The Android application displays the area where the object of interest is with the coordinates of the closest point in the scene. It then calculates the correct vibration sequence to run to convey the location of this object to the user.
