#!/bin/sh

echo 'Running program for ' $1 '...'

if [ "$1" == "webcams" ]; then
    python webcams_main.py 2 0
elif [ "$1" == "capture" ]; then
	python capture_main.py 2 0 2 output/
elif [ "$1" == "disparity" ]; then
	python disparity_main.py input/ output/left_1.jpg output/right_1.jpg disparity/ --bm_settings settings.json
elif [ "$1" == "tuner" ]; then
	python tuner_main.py input/ output/ --bm_settings settings.json
elif [ "$1" == "point_cloud" ]; then
	python point_cloud_main.py input/ images/cam_left.jpg images/cam_right.jpg pointCloud.ply --bm_settings settings.json
elif [ "$1" == "disparity_live" ]; then
	python capture_and_disparity_main.py 2 0 disparity/ input/ images/ settings.json pointCloud.ply
fi